namespace housing.estate.spider
{
    public class Estate
    {
        public string Name;
        public double Price;
        public double Area;

        public override string ToString()
        {
            return string.Format("{0},{1},{2}", Name, Price, Area);
        }
    }
}