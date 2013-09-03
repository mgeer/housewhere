using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;

namespace housing.estate.spider
{
    public class HomeLinkSublinksAcquisition
    {
        private readonly string content;

        public HomeLinkSublinksAcquisition(string content)
        {
            this.content = content;
        }

        public IEnumerable<string> Acquire()
        {
            //<a class='blue_jh' href='/xiaoqu/pg2/'>2</a>
            var regex = new Regex("<a\\s*class='blue_jh'\\s*href='(?<sublink>.*?)'>.*?</a>");
            var matches = regex.Matches(content);
            return from Match matchInType in matches select SiteRoots.HomeLink + matchInType.Groups["sublink"].Value;
        }
    }
}