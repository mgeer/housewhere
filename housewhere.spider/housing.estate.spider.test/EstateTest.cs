using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace housing.estate.spider.test
{
    [TestClass]
    public class EstateTest
    {
        [TestMethod]
        public void it_overrides_to_string()
        {
            var estate = new Estate {Name = "name", Price = 30000, Area = 480000};
            Assert.AreEqual("name,30000,480000", estate.ToString());
        }
    }
}