using System;
using System.Text;

namespace X3.Spider
{
    public class EncodingUtil
    {
        public static Encoding TryGetEncoding(string encoding)
        {
            try
            {
                return Encoding.GetEncoding(encoding);
            }
            catch (Exception)
            {
                return null;
            }
        }
    }
}