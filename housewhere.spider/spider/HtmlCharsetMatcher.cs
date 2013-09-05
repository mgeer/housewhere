using System.Text;
using System.Text.RegularExpressions;

namespace X3.Spider
{
    public class HtmlCharsetMatcher
    {
        readonly Regex charsetExpression = new Regex("charset\\s*=\\s*\"?(?<charset>.*?)\"",
                                                     RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.IgnorePatternWhitespace | RegexOptions.Singleline);

        public Encoding Match(string html)
        {
            var match = charsetExpression.Match(html);
            var value = match.Groups["charset"].Value;
            return string.IsNullOrEmpty(value)
                       ? null
                       : EncodingUtil.TryGetEncoding(value);
        }
    }
}