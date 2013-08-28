using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CorsswordSolver
{
    public class Point
    {
        public int y;
        public int x;

        public Point(int y, int x)
        {
            this.y = y;
            this.x = x;
        }

        public bool isVaild()
        {
            return y != int.MaxValue && x != int.MaxValue;
        }

        public override string ToString()
        {
            return string.Format("y={0},x={1}", y, x);
        }
    }

}
