import os
from datetime import datetime
from ChatExchange.chatexchange.client import Client
import HTMLParser


class GlobalVars:
    false_positives = []
    whitelisted_users = []
    blacklisted_users = []
    ignored_posts = []
    auto_ignored_posts = []
    startup_utc = datetime.utcnow().strftime("%H:%M:%S")
    latest_questions = []
    blockedTime = 0
    charcoal_room_id = "11540"
    meta_tavern_room_id = "89"
    site_filename = { "electronics.stackexchange.com" : "ElectronicsGood.txt",
                      "gaming.stackexchange.com" : "GamingGood.txt", "german.stackexchange.com" : "GermanGood.txt",
                      "italian.stackexchange.com" : "ItalianGood.txt", "math.stackexchange.com" : "MathematicsGood.txt",
                      "spanish.stackexchange.com" : "SpanishGood.txt", "stats.stackexchange.com" : "StatsGood.txt" }
    parser = HTMLParser.HTMLParser()
    wrap = Client("stackexchange.com")
    wrapm = Client("meta.stackexchange.com")
    privileged_users = { charcoal_room_id: ["117490", "66258", "31768","103081","73046","88521","59776", "31465"],
                         meta_tavern_room_id: ["259867", "244519", "244382", "194047", "158100", "178438", "237685",
                                               "215468", "229438", "180276", "161974", "244382", "186281", "266094",
                                               "245167", "230261", "213575", "241919", "203389"] }
    smokeDetector_user_id = { charcoal_room_id: "120914", meta_tavern_room_id: "266345" }
    commit = os.popen("git log --pretty=format:'%h' -n 1").read()
    commit_with_author = os.popen("git log --pretty=format:'%h (%cn: *%s*)' -n 1").read()
    on_master = os.popen("git rev-parse --abbrev-ref HEAD").read().strip() == "master"
    charcoal_hq = None
    tavern_on_the_meta = None
    s = ""
    s_reverted = ""
    specialrooms = []
    bayesian_testroom = None
    apiquota = -1
    bodyfetcher = None
