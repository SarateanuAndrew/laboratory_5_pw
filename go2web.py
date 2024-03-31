import argparse
from bs4 import BeautifulSoup
import re
import requests


def make_request(url):
    return requests.get(url).text


def search(term):
    search_url = f"https://999.md/ro/search?query={term}"
    response_of_999 = requests.get(search_url)
    soup = BeautifulSoup(response_of_999.text, 'html.parser')
    pattern = r'^/ro/\d{8}$'
    count = 1
    for link in soup.find_all('a'):
        href = link.get('href')
        if href and re.match(pattern, href) and len(link.text.strip()) > 1 and count <= 10:
            print(f"{count}. {link.text} - https://999.md{link['href']}")
            count += 1


parser = argparse.ArgumentParser()
group = parser.add_mutually_exclusive_group(required=True)
group.add_argument("-u", "--url_to_platform", help="HTTP request to one URL")
group.add_argument("-s", "--search_word", help="HTTP request to search the term on 999.md")

args = parser.parse_args()

if args.url_to_platform:
    response = make_request(args.url_to_platform)
    print(response)
elif args.search_word:
    search(args.search_word)
