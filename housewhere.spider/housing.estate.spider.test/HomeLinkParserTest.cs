using System;
using System.IO;
using System.Linq;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using X3.Spider;

namespace housing.estate.spider.test
{
    [TestClass]
    public class HomeLinkParserTest
    {
        [TestMethod]
        public void integration_test()
        {
            var spider = new Spider();
            var content = spider.Grab("http://beijing.homelink.com.cn/xiaoqu");
            var estateAcquisition = new EstateAcquisition(content);
            var sublinksAcquisition = new HomeLinkSublinksAcquisition(content);
            var housingEstateLinks = estateAcquisition.Acquire(spider);
            Assert.AreEqual(10, housingEstateLinks.Count());
            var subLinks = sublinksAcquisition.Acquire();
            Assert.AreEqual(8, subLinks.Count());
        }

        [TestMethod]
        public void real_test()
        {
            var homeLinkSpider = new HomeLinkSpider();
            const string outputFile = @"D:\android\housewhere.spider\data\estates.txt";
            using (var fileStream = new FileStream(outputFile, FileMode.OpenOrCreate, FileAccess.ReadWrite, FileShare.Read))
            {
                homeLinkSpider.Spide(est =>
                {
                    var bytes = Encoding.UTF8.GetBytes(est + Environment.NewLine);
                    fileStream.Write(bytes, 0, bytes.Length);
                    fileStream.Flush();
                });
            }
        }

        [TestMethod]
        public void it_should_parse_all_estate_in_one_page_of_home_link()
        {
            var mockedSpider = new Mock<ISpider>();
            mockedSpider.Setup(spider => spider.Grab(It.IsAny<string>())).Returns(Resource.home_link_estate_detail);

            var acquisition = new EstateAcquisition(Resource.home_link_page_content);
            var housingEstateLinks = acquisition.Acquire(mockedSpider.Object);
            Assert.AreEqual(10, housingEstateLinks.Count());
            AssertNameLink("天通西苑三区", 18927, 490000, housingEstateLinks.ElementAt(0));
            AssertNameLink("北京像素北区", 33704, 490000, housingEstateLinks.ElementAt(5));
            AssertNameLink("荣丰2008", 45973, 490000, housingEstateLinks.ElementAt(9));
        }

        private static void AssertNameLink(string expectedName, double expectedPrice, double expectedArea, Estate estate)
        {
            Assert.AreEqual(expectedName, estate.Name);
            Assert.AreEqual(expectedPrice, estate.Price);
            Assert.AreEqual(expectedArea, estate.Area);
        }
    }
}