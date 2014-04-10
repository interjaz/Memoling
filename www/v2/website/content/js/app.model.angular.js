function MemoBase() {
    this.memoBaseId = guid()
    this.name = ""
    this.languages = null
    this.count = 0
    this.created = Math.round(new Date().getTime()/1000);
    this.active = "true";
}

function Word() {
    this.wordId = guid()
    this.word = ""
    this.languageIso639 = null;
    this.description = ""
}


function Memo() {
    this.memoId = guid();
    this.memoBaseId = null;
    this.wordAId = null;
    this.wordBId = null;
    this.created = Math.round(new Date().getTime()/1000);
    this.lastReviewed = Math.round(new Date().getTime()/1000);
    this.displayed = 0;
    this.correctAnsweredWordA = 0;
    this.correctAnsweredWordB = 0;
    this.active = "true";
    
    this.wordA = new Word();
    this.wordB = new Word();
}