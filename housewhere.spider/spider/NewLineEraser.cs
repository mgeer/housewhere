namespace X3.Spider
{
    public class NewLineEraser
    {
        public string Filter(string text)
        {
            return text.Replace("\n", " ");
        }
    }
}