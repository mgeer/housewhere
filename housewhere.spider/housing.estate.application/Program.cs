using System;
using System.IO;
using System.Text;
using housing.estate.spider;

namespace housing.estate.application
{
    class Program
    {
        static void Main(string[] args)
        {
            var homeLinkSpider = new HomeLinkSpider();
            const string outputFile = @".\estates.txt";
            using (var fileStream = new FileStream(outputFile, FileMode.OpenOrCreate, FileAccess.ReadWrite, FileShare.Read))
            {
                using (var writer = new StreamWriter(fileStream, Encoding.UTF8))
                {
                    homeLinkSpider.Spide(est =>
                    {
                        writer.Write(est + Environment.NewLine);
                        writer.Flush();
                    });    
                }
                
            }
        }
    }
}
