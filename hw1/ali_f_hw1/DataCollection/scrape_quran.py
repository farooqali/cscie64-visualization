import re, util, string

DELIMITER = "\t"
URL_TEMPLATE = "http://www.usc.edu/schools/college/crcc/engagement/resources/texts/muslim/quran/%(chapter_number)s.qmt.html"
TRANSLATOR = "YUSUFALI"
VERSE_MATCHING_REGEX = "<strong>%(translator)s:</strong>([^<]+?)<br\s?/>" % {'translator': TRANSLATOR}
words = {}
FIRST_CHAPTER = 1
LAST_CHAPTER = 114
STOP_WORDS = map(string.rstrip, open('stop_words.txt').readlines())

# Scrape 114 chapters in the Qur'an
for chapter_number in range(FIRST_CHAPTER,LAST_CHAPTER+1):
    url = URL_TEMPLATE % {'chapter_number': str(chapter_number).zfill(3)}
    soup = util.mysoupopen(url)

    verse_matches = re.findall(VERSE_MATCHING_REGEX, str(soup))
    
    verse_number = 1
    # for each verse in chapter_number
    for verse_match in verse_matches:
        # remove punctuations and leading/trailing spaces
        verse_text = re.sub("'|,|;|:|'|\"|\!|\?|\.|\(|\)|\-", " ", verse_match).strip()
        verse_text = re.sub("\s{2,5}|\n", " ", verse_text)
        
        # add verse's words to dictionary
        for word in re.split("\s+", verse_text):
          word = word.lower()
          if(word in STOP_WORDS):
            continue
          if(word not in words):
            words[word] = 0
          words[word] += 1
          
        verse_number += 1

print "Word" + DELIMITER + "Occurrences"
for word in sorted(words.keys()):
  print word + DELIMITER + str(words[word])
