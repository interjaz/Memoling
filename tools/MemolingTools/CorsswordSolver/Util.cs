using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CorsswordSolver
{
    class Util
    {
        public static int[] copy(int[] data)
        {
            int[] copy = new int[data.Length];
            for (int i = 0; i < data.Length; i++)
            {
                copy[i] = data[i];
            }

            return copy;
        }

        public static List<MatrixWord> copy(List<MatrixWord> data)
        {
            List<MatrixWord> copy = new List<MatrixWord>(data.Count);
            for (int i = 0; i < data.Count; i++)
            {
                MatrixWord word = data[i];
                copy.Add(new MatrixWord(word.word, new Point(word.from.y, word.from.x), new Point(word.to.y, word.to.x)));
            }

            return copy;
        }

        public static List<Point> copy(List<Point> data)
        {
            List<Point> copy = new List<Point>(data.Count);

            for (int i = 0; i < data.Count; i++)
            {
                copy.Add(new Point(data[i].y, data[i].x));
            }

            return copy;
        }

        public static Matrix copy(Matrix data)
        {
            Matrix copy = new Matrix(data.size);
            for (int i = 0; i < data.size.y; i++)
            {
                for (int j = 0; j < data.size.x; j++)
                {
                    copy.matrix[i][j] = data.matrix[i][j];
                }
            }

            copy.isEmpty = data.isEmpty;
            copy.words = Util.copy(data.words);
            copy.size.x = data.size.x;
            copy.size.y = data.size.y;

            return copy;
        }

        public static List<string> copy(List<string> data)
        {
            List<string> copy = new List<string>(data.Count);
            for (int i = 0; i < data.Count; i++)
            {
                copy.Add(data[i]);
            }

            return copy;
        }

        public static List<string> diff(List<MatrixWord> a, List<string> b)
        {
            List<string> c = Util.copy(b);
            List<string> diff = new List<string>();
            for (int i = 0; i < a.Count; i++)
            {
                c.Remove(a[i].word);
            }

            return c;
        }

        public static List<Matrix> add(Matrix from, List<Matrix> to)
        {
            if (from != null)
            {
                to.Add(from);
            }

            return to;
        }

        public static List<Matrix> add(List<Matrix> from, List<Matrix> to)
        {
            if (from == null)
            {
                return to;
            }

            for (int i = 0; i < from.Count; i++)
            {
                Matrix m = from[i];
                to.Add(m);
            }

            return to;
        }

        public static bool nextPermutation(int[] numList)
        {
            var largestIndex = -1;
            for (var i = numList.Length - 2; i >= 0; i--)
            {
                if (numList[i] < numList[i + 1])
                {
                    largestIndex = i;
                    break;
                }
            }

            if (largestIndex < 0) return false;

            var largestIndex2 = -1;
            for (var i = numList.Length - 1; i >= 0; i--)
            {
                if (numList[largestIndex] < numList[i])
                {
                    largestIndex2 = i;
                    break;
                }
            }

            var tmp = numList[largestIndex];
            numList[largestIndex] = numList[largestIndex2];
            numList[largestIndex2] = tmp;

            for (int i = largestIndex + 1, j = numList.Length - 1; i < j; i++, j--)
            {
                tmp = numList[i];
                numList[i] = numList[j];
                numList[j] = tmp;
            }

            return true;
        }

    }
}
