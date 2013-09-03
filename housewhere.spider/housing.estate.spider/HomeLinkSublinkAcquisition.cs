using System.Collections.Generic;
using System.Text.RegularExpressions;

namespace housing.estate.spider
{
    public class HomeLinkSublinkAcquisition
    {
        private readonly string content;

        public HomeLinkSublinkAcquisition(string content)
        {
            this.content = content;
        }

        public IEnumerable<string> Acquire()
        {
            //<a class='blue_jh' href='/xiaoqu/pg2/'>2</a>
            var regex = new Regex("<a\\s*class='blue_jh'\\s*href='(?<sublink>.*?)'>.*?</a>");
            var matches = regex.Matches(content);
            foreach (var match in matches)
            {
                var matchInType = (Match)match;
                yield return SiteRoots.HomeLink +  matchInType.Groups["sublink"].Value;
            }
        }
    }
}