package app.memoling.android.crossword;

public class MatrixWord
{
    public String word;
    public Point from;
    public Point to;

    public MatrixWord()
    {
    }

    public MatrixWord(String word, Point from, Point to)
    {
        this.word = word;
        this.from = from;
        this.to = to;
    }

    public void move(Point pos)
    {
        from = new Point(from.y + pos.y, from.x + pos.x);
        to = new Point(to.y + pos.y, to.x + pos.x);
    }

    @Override
    public String toString()
    {
        return word + ": " + from + ", " + to;
    }
}
