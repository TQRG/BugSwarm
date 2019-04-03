from spamhandling import *
from chatcommunicate import *
from datahandling import *
from spamhandling import handlespam

class BodyFetcher:
    queue = {}

    specialCases = {"stackoverflow.com" : 5, "serverfault.com" : 5, "superuser.com" : 5, "drupal.stackexchange.com" : 1, "meta.stackexchange.com" : 1}

    threshold = 2

    def addToQueue(self, post):
        d=json.loads(json.loads(post)["data"])
        sitebase = d["siteBaseHostAddress"]
        postid = d["id"]
        if sitebase in self.queue:
            self.queue[sitebase].append(postid)
        else:
            self.queue[sitebase] = [postid]

        print self.queue
        self.checkQueue()

    def checkQueue(self):
        for site, values in self.queue.iteritems():
            if site in self.specialCases:
                if len(self.queue[site]) >= self.specialCases[site]:
                    print "site " + site + " met special case quota, fetching..."
                    self.makeApiCallForSite(site)
                    return

        # if we don't have any sites with their queue filled, take the first one without a special case
        for site, values in self.queue.iteritems():
            if site not in self.specialCases and len(values) >= self.threshold:
                self.makeApiCallForSite(site)
                return

    def printQueue(self):
        string = ""
        for site, values in self.queue.iteritems():
            string = string + "\n" + site + ": " + str(len(values))

        return string

    def makeApiCallForSite(self, site):
        posts = self.queue.pop(site)
        url = "http://api.stackexchange.com/2.2/questions/" + ";".join(str(x) for x in posts)  + "?site=" + site + "&filter=!4y_-sca-)pfAwlmP_1FxC6e5yzutRIcQvonAiP&key=IAkbitmze4B8KpacUfLqkw(("
        response = requests.get(url).json()

        GlobalVars.apiquota = response["quota_remaining"]

        for post in response["items"]:
            title = GlobalVars.parser.unescape(post["title"])
            body = GlobalVars.parser.unescape(post["body"])
            owner_name = post["owner"]["display_name"]
            link = post["link"]
            try:
                owner_link = post["owner"]["link"]
            except:
                owner_link = ""
            try:
                owner_rep = post["owner"]["reputation"]
            except:
                owner_rep = 0
            q_id = post["question_id"]

            if owner_rep <= 50 and checkifspam(title, body, owner_name, owner_link, site, q_id, link):
                try:
                    handlespam(title, body, owner_name, site, link, owner_link, q_id)
                except:
                    print "NOP"
            try:
                for answer in post["answers"]:
                    answer_title = ""
                    body = answer["body"]
                    owner_name = answer["owner"]["display_name"]
                    print "got answer from owner with name " + owner_name
                    link = answer["link"]
                    owner_link = answer["owner"]["link"]
                    a_id = answer["answer_id"]
                    try:
                        owner_rep = answer["owner"]["reputation"]
                    except:
                        owner_rep = 0

                    if owner_rep <= 50 and checkifspam(answer_title, body, owner_name, owner_link, site, a_id, link):
                        try:
                            handlespam(title, body, owner_name, site, link, owner_link, a_id)
                        except:
                            print "NOP"
            except:
                print "no answers"
