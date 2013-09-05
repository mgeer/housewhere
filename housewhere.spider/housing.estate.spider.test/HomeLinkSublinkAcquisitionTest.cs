using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace housing.estate.spider.test
{
    [TestClass]
    public class HomeLinkSublinkAcquisitionTest
    {
        [TestMethod]
        public void it_should_parse_sub_links()
        {
            var acquisition = new HomeLinkSublinksAcquisition(Resource.home_link_page_content);
            var subLinks = acquisition.Acquire();
            Assert.AreEqual(8, subLinks.Count());
            Assert.AreEqual(GetFullLinkOfHomeLink("/xiaoqu/pg2/"), subLinks.ElementAt(0));
            Assert.AreEqual(GetFullLinkOfHomeLink("/xiaoqu/pg5/"), subLinks.ElementAt(3));
            Assert.AreEqual(GetFullLinkOfHomeLink("/xiaoqu/pg7/"), subLinks.ElementAt(5));
            Assert.AreEqual(GetFullLinkOfHomeLink("/xiaoqu/pg9/"), subLinks.ElementAt(7));
        }

        private static string GetFullLinkOfHomeLink(string subLink)
        {
            return SiteRoots.HomeLink + subLink;
        }
    }
}