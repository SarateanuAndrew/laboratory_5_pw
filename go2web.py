import argparse
from bs4 import BeautifulSoup
import re
import requests


def make_request(url):
    response_of_url = requests.get(url)
    return response_of_url.text


def search(term):
    search_url = f"https://999.md/ro/search?query={term}"
    response_of_999 = requests.get(search_url)
    soup = BeautifulSoup(response_of_999.text, 'html.parser')

    # Extract results from first search page
    pattern = r'^/ro/\d{8}$'
    count = 1
    for link in soup.find_all('a'):
        href = link.get('href')
        if href and re.match(pattern, href) and len(link.text.strip()) > 1 and count <= 10:
            print(f"{count}. {link.text} - https://999.md{link['href']}")
            count += 1


parser = argparse.ArgumentParser(
    description="CLI utility for making HTTP requests and displaying responses from a specific URL")
group = parser.add_mutually_exclusive_group(required=True)
group.add_argument("-u", "--url", help="Make an HTTP request to the specified URL")
group.add_argument("-s", "--search", help="Make an HTTP request to search the term on 999.md platform")

args = parser.parse_args()

if args.url:
    response = make_request(args.url)
    print(response)
elif args.search:
    search(args.search)
