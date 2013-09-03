using System.Collections.Generic;
using System.Text.RegularExpressions;
using X3.Spider;

namespace housing.estate.spider
{
    public class EstateAcquisition
    {
        private readonly string content;

        public EstateAcquisition(string content)
        {
            this.content = content;
        }

        public IEnumerable<Estate> Acquire(ISpider spider)
        {
            //<strong class="font14"><span><a href="/c-xisilaigongguan8830/" target="_blank">西斯莱公馆</a>
            //<p><span>45973</span>元/平米</p>
            var regex = new Regex("<strong\\s*class=\"font14\".*?<a\\s*href=\"(?<link>.*?)\".*?>(?<name>.*?)<\\/a>(.|\n)*?<p><span>(?<price>\\d+)</span>元/平米", RegexOptions.Compiled);
            var matches = regex.Matches(content);
            foreach (var match in matches)
            {
                var matchInType = (Match) match;
                var link =SiteRoots.HomeLink + matchInType.Groups["link"].Value;
                var name = matchInType.Groups["name"].Value;
                var priceText = matchInType.Groups["price"].Value;
                yield return new Estate {Name = name, Price = TryParseDouble(priceText), Area = GetArea(spider, link)};
            }
        }

        private double GetArea(ISpider spider, string link)
        {
            var estateDetail = spider.Grab(link);
//            <dd class="titlew">占地面积</dd>
//            <dd class="tidfes">490000平米&nbsp;</dd>
            var regex = new Regex("(?<=<dd\\s*class=\"titlew\">占地面积</dd>(.|\n)*?<dd\\s*class=\"tidfes\">)\\d+(?=平米)", RegexOptions.Compiled);
            var match = regex.Match(estateDetail);
            return TryParseDouble(match.Value);
        }

        private static double TryParseDouble(string text)
        {
            double price;
            double.TryParse(text, out price);
            return price;
        }

        public IEnumerable<string> AcquireSubLinks()
        {
            //<a class='blue_jh' href='/xiaoqu/pg2/'>2</a>
            var regex = new Regex("<a\\s*class='blue_jh'\\s*href='(?<sublink>.*?)'>.*?</a>");
            var matches = regex.Matches(content);
            foreach (var match in matches)
            {
                var matchInType = (Match) match;
                yield return matchInType.Groups["sublink"].Value;
            }
        }
    }
}