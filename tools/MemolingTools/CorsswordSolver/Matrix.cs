using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CorsswordSolver
{
    //               y x x x
    // [y][x]        y x x x 
    //               y x x x
    //
    public class Matrix
    {
        public List<List<char>> matrix;
        public List<MatrixWord> words = new List<MatrixWord>();
 

        public Point size;
        public bool isEmpty = true;
        
        public Matrix(Point p)
        {
            size = p;

            matrix = new List<List<char>>(size.y);
            for (int i = 0; i < size.y; i++)
            {
                matrix.Add(new List<char>(size.x));
                for (int j = 0; j < size.x; j++)
                {
                    matrix[i].Add('-');
                }
            }
        }

        public bool hasWord(string word)
        {
            for (int i = 0; i < words.Count; i++)
            {
                if (word.Equals(words[i].word))
                {
                    return true;
                }
            }

            return false;
        }

        public void setHWord(string word, Point pos)
        {
            for (int i = 0; i < word.Length; i++)
            {
                matrix[pos.y][pos.x + i] = word[i];
            }
            isEmpty = false;

            words.Add(new MatrixWord(word, pos, new Point(pos.y, pos.x+word.Length-1)));
        }

        public void setVWord(string word, Point pos)
        {
            for (int i = 0; i < word.Length; i++)
            {
                matrix[pos.y + i][pos.x] = word[i];
            }
            isEmpty = false;

            words.Add(new MatrixWord(word, pos, new Point(pos.y + word.Length-1, pos.x)));
        }

        public void setLDWord(string word, Point pos)
        {
            for (int i = 0; i < word.Length; i++)
            {
                matrix[pos.y + i][pos.x + i] = word[i];
            }
            isEmpty = false;

            words.Add(new MatrixWord(word, pos, new Point(pos.y + word.Length-1, pos.x + word.Length-1)));
        }

        public void setRDWord(string word, Point pos)
        {
            for (int i = 0; i < word.Length; i++)
            {
                matrix[pos.y + i][pos.x - i] = word[i];
            }
            isEmpty = false;

            words.Add(new MatrixWord(word, pos, new Point(pos.y + word.Length-1, pos.x - (word.Length-1))));
        }

        public Point findChar(char c, Point pos)
        {
            for (int i = pos.y; i < size.y; i++)
            {
                for (int j = pos.x; j < size.x; j++)
                {
                    if (matrix[i][j] == c)
                    {
                        return new Point(i, j);
                    }
                }
            }

            return new Point(int.MaxValue, int.MaxValue);
        }

        public bool willHFit(string word, Point pos)
        {
            int x1 = pos.x + word.Length;
            if (pos.y < 0 || pos.x < 0 || pos.y > size.y || x1 > size.x)
            {
                return false;
            }

            if (hasWord(word))
            {
                return false;
            }

            for (int i = 0; i < word.Length; i++)
            {
                char c = matrix[pos.y][pos.x + i];
                if (c != '-' && c != word[i])
                {
                    return false;
                }
            }

            return true;
        }

        public bool willVFit(string word, Point pos)
        {
            int y1 = pos.y + word.Length;
            if (pos.y < 0 || pos.x < 0 || y1 > size.y || pos.x > size.x)
            {
                return false;
            }

            if (hasWord(word))
            {
                return false;
            }

            for (int i = 0; i < word.Length; i++)
            {
                char c = matrix[pos.y + i][pos.x];
                if (c != '-' && c != word[i])
                {
                    return false;
                }
            }

            return true;
        }

        public bool willLDFit(string word, Point pos)
        {
            int y1 = pos.y + word.Length;
            int x1 = pos.x + word.Length;
            if (pos.y < 0 || pos.x < 0 || y1 > size.y || x1 > size.x)
            {
                return false;
            }

            if (hasWord(word))
            {
                return false;
            }

            for (int i = 0; i < word.Length; i++)
            {
                char c = matrix[pos.y + i][pos.x + i];
                if (c != '-' && c != word[i])
                {
                    return false;
                }
            }

            return true;
        }

        public bool willRDFit(string word, Point pos)
        {
            int y1 = pos.y + word.Length;
            int x1 = pos.x - (word.Length - 1);
            if (pos.y < 0 || pos.x < 0 || y1 > size.y || x1 < 0)
            {
                return false;
            }

            if (hasWord(word))
            {
                return false;
            }

            for (int i = 0; i < word.Length; i++)
            {
                char c = matrix[pos.y + i][pos.x - i];
                if (c != '-' && c != word[i])
                {
                    return false;
                }
            }

            return true;
        }
        
        public Tuple<Point, Point> bounds()
        {
            int minX=int.MaxValue, minY = int.MaxValue;
            int maxX=-1, maxY= -1;

            for (int y = 0; y < size.y; y++)
            {
                for (int x = 0; x < size.x; x++)
                {
                    if (matrix[y][x] != '-')
                    {
                        minY = y <  minY?y:minY;
                        minX = x <  minX?x:minX;
                        maxY = y >= maxY?y:maxY;
                        maxX = x >= maxX?x:maxX;
                    }
                }
            }

            return new Tuple<Point, Point>(new Point(minY, minX), new Point(maxY, maxX));
        }

        public Point maxHSpace(int y)
        {
            int max = 0;
            int maxPos = 0;
            int current = 0;
            int currentPos = 0;
            for (int i = 0; i < size.x; i++)
            {
                if (matrix[y][i] == '-')
                {
                    current++;
                }
                else
                {
                    if (current > max)
                    {
                        max = current;
                        maxPos = currentPos;
                    }
                    current = 0;
                    currentPos = i + 1;
                }
            }

            if (current > max)
            {
                max = current;
                maxPos = currentPos;
            }

            return new Point(max, maxPos);
        }

        public Point maxVSpace(int x)
        {
            int max = 0;
            int maxPos = 0;
            int current = 0;
            int currentPos = 0;
            for (int i = 0; i < size.y; i++)
            {
                if (matrix[i][x] == '-')
                {
                    current++;
                }
                else
                {
                    if (current > max)
                    {
                        max = current;
                        maxPos = currentPos;
                    }
                    current = 0;
                    currentPos = i + 1;
                }
            }

            if (current > max)
            {
                max = current;
                maxPos = currentPos;
            }

            return new Point(maxPos, max);
        }

        public void move(Point pos)
        {
            Matrix m = new Matrix(size);
            for (int i = 0; i < size.y; i++)
            {
                for (int j = 0; j < size.x; j++)
                {
                    bool yOut = i - pos.y < 0 || i - pos.y >= size.y;
                    bool xOut = j - pos.x < 0 || j - pos.x >= size.x;
                    if (!yOut && !xOut)
                    {
                        if (m.matrix[i][j] == '-')
                        {
                            m.matrix[i][j] = matrix[i - pos.y][j - pos.x];
                        }
                    }

                }
            }

            matrix = m.matrix;
            
            for (int i = 0; i < words.Count; i++) {
                words[i].move(pos);
            }
        }

        public void print()
        {
            Console.WriteLine(ToString());
            Console.WriteLine();
        }
        
        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.AppendLine();
            for (int i = 0; i < size.y; i++)
            {
                if (i == 0)
                {
                    sb.Append("  ");
                    for (int a = 0; a < size.x; a++)
                    {
                        sb.Append(" " + a + " ");
                    }
                    sb.AppendLine();
                }

                for (int j = 0; j < size.x; j++)
                {
                    if (j == 0)
                    {
                        sb.Append(i + " ");
                    }

                    sb.Append(" " + matrix[i][j].ToString() + " ");
                }
                sb.AppendLine();
            }
            return sb.ToString();
        }
    }
}
