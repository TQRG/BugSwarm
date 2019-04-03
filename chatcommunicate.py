import random
import requests
import time
from parsing import *
from datahandling import *
from bayesianfuncs import *
from globalvars import GlobalVars


def post_message_in_room(room_id_str, msg):
    if room_id_str == GlobalVars.charcoal_room_id:
        GlobalVars.charcoal_hq.send_message(msg)
    elif room_id_str == GlobalVars.meta_tavern_room_id:
        GlobalVars.tavern_on_the_meta.send_message(msg)


def watcher(ev, wrap2):
    if ev.type_id != 1:
        return
    print(ev)
    ev_room = str(ev.data["room_id"])
    ev_user_id = str(ev.data["user_id"])
    message_parts = ev.message.content_source.split(" ")
    second_part_lower = "" if len(message_parts) < 2 else message_parts[1].lower()
    content_lower = ev.content.lower()
    if re.compile(":[0-9]+").search(message_parts[0]):
        if (second_part_lower.startswith("false") or second_part_lower.startswith("fp")) \
                and is_privileged(ev_room, ev_user_id):
            try:
                should_delete = True
                msg_id = int(message_parts[0][1:])
                msg_content = None
                msg_to_delete = wrap2.get_message(msg_id)
                if str(msg_to_delete.owner.id) == GlobalVars.smokeDetector_user_id[ev_room]:
                    msg_content = msg_to_delete.content_source
                if msg_content is not None:
                    site_post_id = fetch_post_id_and_site_from_msg_content(msg_content)
                    add_false_positive(site_post_id)
                    user_added = False
                    if message_parts[1].lower().startswith("falseu") or message_parts[1].lower().startswith("fpu"):
                        url_from_msg = fetch_owner_url_from_msg_content(msg_content)
                        user = get_user_from_url(url_from_msg)
                        add_whitelisted_user(user)
                        user_added = True
                    learned = bayesian_learn_title(msg_content, "good")
                    if learned:
                        if user_added and site_post_id is not None:
                            ev.message.reply("Registered as false positive, added title to Bayesian doctype 'good', whitelisted user.")
                        elif site_post_id is not None:
                            ev.message.reply("Registered as false positive and added title to Bayesian doctype 'good'.")
                        else:
                            ev.message.reply("Could not register title as false positive.")
                            should_delete = False
                    else:
                        if user_added and site_post_id is not None:
                            ev.message.reply("Registered as false positive and whitelisted user, but could not add the title to the Bayesian doctype 'good'.")
                        elif site_post_id is not None:
                            ev.message.reply("Registered as false positive, but could not add the title to the Bayesian doctype 'good'.")
                        else:
                            ev.message.reply("Could not register title as false positive.")
                            should_delete = False
                    if should_delete:
                        msg_to_delete.delete()
            except:
                pass # couldn't delete message
        if (second_part_lower.startswith("true") or second_part_lower.startswith("tp")) \
                and is_privileged(ev_room, ev_user_id):
            try:
                msg_id = int(message_parts[0][1:])
                msg_content = None
                msg_true_positive = wrap2.get_message(msg_id)
                if str(msg_true_positive.owner.id) == GlobalVars.smokeDetector_user_id[ev_room]:
                    msg_content = msg_true_positive.content_source
                if msg_content is not None:
                    learned = bayesian_learn_title(msg_content, "bad")
                    user_added = False
                    if message_parts[1].lower().startswith("trueu") or message_parts[1].lower().startswith("tpu"):
                        url_from_msg = fetch_owner_url_from_msg_content(msg_content)
                        user = get_user_from_url(url_from_msg)
                        add_blacklisted_user(user)
                        user_added = True
                    if learned:
                        if user_added:
                            ev.message.reply("Registered as true positive: added title to Bayesian doctype 'bad' and blacklisted user.")
                        else:
                            ev.message.reply("Registered as true positive: added title to Bayesian doctype 'bad'.")
                    else:
                        if user_added:
                            ev.message.reply("User blacklisted, but something went wrong when registering title as true positive.")
                        else:
                            ev.message.reply("Something went wrong when registering title as true positive.")
            except:
                pass
        if second_part_lower.startswith("ignore") and is_privileged(ev_room, ev_user_id):
            try:
                msg_id = int(message_parts[0][1:])
                msg_content = None
                msg_ignore = wrap2.get_message(msg_id)
                if(str(msg_ignore.owner.id) == GlobalVars.smokeDetector_user_id[ev_room]):
                    msg_content = msg_ignore.content_source
                if(msg_content is not None):
                    post_id_site = fetch_post_id_and_site_from_msg_content(msg_content)
                    add_ignored_post(post_id_site)
                    ev.message.reply("Post ignored; alerts about it will not longer be posted.")
            except:
                pass
        if (second_part_lower == "delete" or second_part_lower == "remove" or second_part_lower == "gone") \
                and is_privileged(ev_room, ev_user_id):
            try:
                msg_id = int(message_parts[0][1:])
                msg_to_delete = wrap2.get_message(msg_id)
                if str(msg_to_delete.owner.id) == GlobalVars.smokeDetector_user_id[ev_room]:
                    msg_to_delete.delete()
            except:
                pass # couldn't delete message
    if content_lower.startswith("!!/wut"):
        ev.message.reply("Whaddya mean, 'wut'? Humans...")
    if content_lower.startswith("!!/lick"):
        ev.message.reply("*licks ice cream cone*")
    if content_lower.startswith("!!/hats"):
        wb_end = datetime(2015, 1, 5, 0, 0, 0)
        now = datetime.utcnow()
        if wb_end > now:
            diff = wb_end - now
            hours, remainder = divmod(diff.seconds, 3600)
            minutes, seconds = divmod(remainder, 60)
            daystr = "days" if diff.days != 1 else "day"
            hourstr = "hours" if hours != 1 else "hour"
            minutestr = "minutes" if minutes != 1 else "minute"
            secondstr = "seconds" if seconds != 1 else "second"
            ev.message.reply("HATS ARE AWESOME. Winter Bash will end in %s %s, %s %s, %s %s and %s %s. :(" %
                             (diff.days, daystr, hours, hourstr, minutes, minutestr, seconds, secondstr))
        else:
            ev.message.reply("WINTERBASH IS OVER! :(")
    if content_lower.startswith("!!/alive"):
        if ev_room == GlobalVars.charcoal_room_id:
            ev.message.reply('Of course')
        elif ev_room == GlobalVars.meta_tavern_room_id:
            ev.message.reply(random.choice(['Yup', 'You doubt me?', 'Of course', '... did I miss something?',
                                            'plz send teh coffee',
                                            'Watching this endless list of new questions *never* gets boring',
                                            'Kinda sorta']))
    if content_lower.startswith("!!/rev"):
            ev.message.reply('[' + GlobalVars.commit_with_author + '](https://github.com/Charcoal-SE/SmokeDetector/commit/'+ GlobalVars.commit +')')
    if content_lower.startswith("!!/status"):
            ev.message.reply('Running since %s UTC' % GlobalVars.startup_utc)
    if content_lower.startswith("!!/reboot"):
        if is_privileged(ev_room, ev_user_id):
            post_message_in_room(ev_room, "Goodbye, cruel world")
            os._exit(5)
    if content_lower.startswith("!!/stappit"):
        if is_privileged(ev_room, ev_user_id):
            post_message_in_room(ev_room, "Goodbye, cruel world")
            os._exit(6)
    if content_lower.startswith("!!/master"):
        if is_privileged(ev_room, ev_user_id):
            ev.message.reply("Checking out to master and restarting...")
            os._exit(8)
    if content_lower.startswith("!!/clearbl"):
        if is_privileged(ev_room, ev_user_id):
            if os.path.isfile("blacklistedUsers.txt"):
                os.remove("blacklistedUsers.txt")
                GlobalVars.blacklisted_users = []
                ev.message.reply("Kaboom, blacklisted users cleared.")
            else:
                ev.message.reply("There are no blacklisted users at the moment.")
    if content_lower.startswith("!!/block"):
        if is_privileged(ev_room, ev_user_id):
            ev.message.reply("blocked")
            timeToBlock = ev.content[9:].strip()
            timeToBlock = int(timeToBlock) if timeToBlock else 0
            if 0 < timeToBlock < 14400:
                GlobalVars.blockedTime = time.time() + timeToBlock
            else:
                GlobalVars.blockedTime = time.time() + 900
    if content_lower.startswith("!!/unblock"):
        if is_privileged(ev_room, ev_user_id):
            GlobalVars.blockedTime = time.time()
            ev.message.reply("unblocked")
    if content_lower.startswith("!!/pull"):
        if is_privileged(ev_room, ev_user_id):
            r = requests.get('https://api.github.com/repos/Charcoal-SE/SmokeDetector/git/refs/heads/master')
            latest_sha = r.json()["object"]["sha"]
            r = requests.get('https://api.github.com/repos/Charcoal-SE/SmokeDetector/commits/' + latest_sha + '/statuses')
            states = []
            for status in r.json():
                state = status["state"]
                states.append(state)
            if "success" in states:
                ev.message.reply("Pulling latest from master -- CI build passed.")
                os._exit(3)
            elif "error" in states or "failure" in states:
                ev.message.reply("CI build failed! :( Please check your commit.")
            elif "pending" in states or not states:
                ev.message.reply("CI build is still pending, wait until the build has finished and then pull again.")
    if content_lower.startswith("!!/help"):
        ev.message.reply("I'm [SmokeDetector](https://github.com/Charcoal-SE/SmokeDetector), a bot that detects spam and low-quality posts on the network and posts alerts to chat.")
    if content_lower.startswith("!!/apiquota"):
        ev.message.reply(GlobalVars.apiquota)
