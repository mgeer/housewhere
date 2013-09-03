using System;
using System.Collections.Generic;
using X3.Spider;

namespace housing.estate.spider
{
    public class HomeLinkSpider
    {
        readonly Spider spider = new Spider();
        const string SEED_URL = SiteRoots.HomeLink + "/xiaoqu";

        private readonly HashSet<string> doneLinks = new HashSet<string>();
        private readonly Queue<string> todoLinks = new Queue<string>(); 

        private readonly HashSet<string> estateNameSet = new HashSet<string>();
        private readonly List<Estate> estateList = new List<Estate>(); 

        public List<Estate> Spide(Action<string> callback)
        {
            todoLinks.Enqueue(SEED_URL);
            while (todoLinks.Count > 0)
            {
                var url = todoLinks.Dequeue();
                if (doneLinks.Contains(url))
                {
                    continue;
                }
                SpideOnePage(url, callback);
                doneLinks.Add(url);
            }
            return estateList;
        }

        private void SpideOnePage(string url, Action<string> callback)
        {
            var content = spider.Grab(url);
            var estateAcquisition = new EstateAcquisition(content);
            var estates = estateAcquisition.Acquire(spider);
            foreach (var estate in estates)
            {
                if (estateNameSet.Contains(estate.Name))
                {
                    continue;
                }
                estateNameSet.Add(estate.Name);
                estateList.Add(estate);
                callback(estate.ToString());
                Console.Out.WriteLine(string.Format("{0} estate acquired=== {1}", estateList.Count, estate));
            }
            var sublinksAcquisition = new HomeLinkSublinksAcquisition(content);
            var subLinks = sublinksAcquisition.Acquire();
            foreach (var subLink in subLinks)
            {
                if (!doneLinks.Contains(subLink))
                {
                    todoLinks.Enqueue(subLink);
                }
            }
        }
    }
}