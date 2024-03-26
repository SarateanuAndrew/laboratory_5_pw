import argparse
import requests


def make_request(url):
    response_of_url = requests.get(url)
    return response_of_url.text


parser = argparse.ArgumentParser(
    description="CLI utility for making HTTP requests and displaying responses from a specific URL")
group = parser.add_mutually_exclusive_group(required=True)
group.add_argument("-u", "--url", help="Make an HTTP request to the specified URL")
group.add_argument("-s", "--search", help="Make an HTTP request to search the term on 999.md platform")

args = parser.parse_args()

if args.url:
    response = make_request(args.url)
    print(response)