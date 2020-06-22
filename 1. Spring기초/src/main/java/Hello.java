public class Hello {
    private String format;
    public String hello(String AnotherMan)
    {
        return String.format(format,AnotherMan);
    }
    public void setFormat(String format)
    {
        this.format=format;
    }
}
