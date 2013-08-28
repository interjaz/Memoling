using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CorsswordSolver
{
    public class MatrixWord
    {
        public string word;
        public Point from;
        public Point to;

        public MatrixWord()
        {
        }

        public MatrixWord(string word, Point from, Point to)
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

        public override string ToString()
        {
            return word + ": " + from + ", " + to;
        }
    }
}
