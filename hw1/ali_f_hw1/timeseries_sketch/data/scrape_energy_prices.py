import re, util, string, datetime, time

# configuration
STARTING_YEAR = 1996
DELIMITER = "\t"
URL_TEMPLATE = "http://www.swivel.com/data_sets/spreadsheet/1000052?page=%(page)s"
PAGES = range(1,3)
DATE_COLUMN_PROPERTIES = {"class": "DateTimeDataFormat "}
RESIDENTIAL_PRICE_COLUMN_PROPERTIES = {'class': 'NumberDataFormat column1001109'}
COMMERCIAL_PRICE_COLUMN_PROPERTIES = {'class': 'NumberDataFormat column1001110'}
INDUSTRIAL_PRICE_COLUMN_PROPERTIES = {'class': 'NumberDataFormat column1001111'}

prices_by_date = {}
print "Month Year" + DELIMITER + "Residential" + DELIMITER + "Commercial" + DELIMITER + "Industrial"
for page in PAGES:
  soup = util.mysoupopen(URL_TEMPLATE % {"page": page})
  table = soup.find("table", {"class": "data"})

  # for each row on page
  for row in table.findAll("tr"):
    date_column = row.find("td", DATE_COLUMN_PROPERTIES)
    if date_column == None: continue
  
    # scrape data from rows and store in prices_by_date
    date_struct = time.strptime(date_column.string.strip(), "%b %Y")
    date = datetime.date(date_struct[0], date_struct[1], 1)
    residential_price = row.find("td", RESIDENTIAL_PRICE_COLUMN_PROPERTIES).string.strip()
    commercial_price = row.find("td", COMMERCIAL_PRICE_COLUMN_PROPERTIES).string.strip()
    industrial_price = row.find("td", INDUSTRIAL_PRICE_COLUMN_PROPERTIES).string.strip()
    if date.year >= STARTING_YEAR:
      prices_by_date[date] = {'residential': residential_price, 'commercial': commercial_price, 'industrial': industrial_price}

# print prices for each date
for date in sorted(prices_by_date.keys()):
  print date.strftime("%b") + " '" + date.strftime("%y") + DELIMITER + \
        prices_by_date[date]['residential'] + DELIMITER + \
        prices_by_date[date]['commercial'] + DELIMITER + \
        prices_by_date[date]['industrial']
