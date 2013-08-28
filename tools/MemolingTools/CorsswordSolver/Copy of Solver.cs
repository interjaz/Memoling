using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CorsswordSolver
{
    class CopySolver
    {
        private bool found = false;

        private int operations = 0;
        private int maxOperations = 1000;

        public bool reachedLimit()
        {
            return operations > maxOperations;
        }

        public bool success()
        {
            return found;
        }

        public List<Matrix> solve(List<string> words, bool allowDiagonal, Matrix matrix)
        {
            List<Matrix> ms = new List<Matrix>();

            int[] perm = new int[words.Count];
            for (int i = 0; i < words.Count; i++)
            {
                perm[i] = i;
            }

            do
            {
                List<string> permWords = new List<string>();
                for (int i = 0; i < words.Count; i++)
                {
                    permWords.Add(words[perm[i]]);
                }

                List<Matrix> lms = lineSolve(permWords, allowDiagonal, matrix);
                if (lms != null)
                {
                    for (int i = 0; i < lms.Count; i++)
                    {
                        ms.Add(lms[i]);
                    }
                }

            } while (!found && !reachedLimit() && Util.nextPermutation(perm));

            return ms;
        }

        public List<Matrix> lineSolve(List<string> words, bool allowDiagonal, Matrix matrix)
        {
            if (reachedLimit())
            {
                return null;
            }

            operations++;

            List<Matrix> mxs = new List<Matrix>();

            for (int i = 0; i < words.Count; i++)
            {
                string word = words[i];
                List<string> vWords = Util.copy(words);
                List<string> hWords = Util.copy(words);
                List<string> dlWords = Util.copy(words);
                List<string> drWords = Util.copy(words);
                vWords.RemoveAt(i);
                hWords.RemoveAt(i);
                dlWords.RemoveAt(i);
                drWords.RemoveAt(i);

                List<Matrix> ms;
                ms = FitHorizontal(vWords, word, allowDiagonal, Util.copy(matrix));
                if (ms != null)
                {
                    Util.add(ms, mxs);
                }

                if (found)
                {
                    return mxs;
                }
                else
                {
                    ms = FitVertical(hWords, word, allowDiagonal, Util.copy(matrix));
                    if (ms != null)
                    {
                        Util.add(ms, mxs);
                    }

                    if (found)
                    {
                        return mxs;
                    }
                }

            }

            return mxs;
        }

        public List<Matrix> FitHorizontal(List<string> words, string word, bool allowDiagonal, Matrix matrix)
        {
            List<Matrix> mxs = new List<Matrix>();
            if (words.Count == 0)
            {
                //             matrix.print();
                found = true;
                Util.add(matrix, mxs);
                return mxs;
            }

            if (matrix.isEmpty)
            {
                Point newPos = new Point(0, 0);
                if (matrix.willHFit(word, newPos))
                {
                    matrix.setHWord(word, new Point(0, 0));
                    Util.add(lineSolve(words, allowDiagonal, matrix), mxs);
                }
                return mxs;
            }

            bool fitted = false;
            for (int i = 0; i < word.Length && !found && !reachedLimit(); i++)
            {
                char c = word[i];
                Point pos = new Point(-1, -1);
                bool goWhile = true;
                while (!found && !reachedLimit() && goWhile)
                {
                    pos = matrix.findChar(c, new Point(pos.y + 1, pos.x + 1));
                    if (pos.y != int.MaxValue && pos.x != int.MaxValue)
                    {
                        if (pos.x - i < word.Length)
                        {
                            Matrix m = Util.copy(matrix);
                            Point newPos = new Point(pos.y, pos.x - i);
                            if (m.willHFit(word, newPos))
                            {
                                m.setHWord(word, newPos);
                                Util.add(lineSolve(words, allowDiagonal, m), mxs);
                                fitted = true;
                            }
                        }
                        else
                        {
                            Tuple<Point, Point> bounds = matrix.bounds();
                            Point tBounds = bounds.Item1;
                            Point bBounds = bounds.Item2;

                            Point top = new Point(tBounds.y, tBounds.x);
                            Point bottom = new Point(matrix.size.y - bBounds.y, matrix.size.y - bBounds.x);

                            if (top.x > i)
                            {
                                Matrix m = Util.copy(matrix);
                                m.move(new Point(0, -i));
                                Point newPos = new Point(pos.y, pos.x - i);
                                if (m.willHFit(word, newPos))
                                {
                                    m.setHWord(word, newPos);
                                    Util.add(lineSolve(words, allowDiagonal, m), mxs);
                                    fitted = true;
                                }
                            }

                            if (bottom.x > word.Length - i)
                            {
                                Matrix m = Util.copy(matrix);
                                m.move(new Point(0, word.Length - i));
                                Point newPos = new Point(pos.y, pos.x + word.Length - i);
                                if (m.willHFit(word, newPos))
                                {
                                    m.setHWord(word, newPos);
                                    Util.add(lineSolve(words, allowDiagonal, m), mxs);
                                    fitted = true;
                                }
                            }
                        }
                    }
                    else
                    {
                        goWhile = false;
                    }
                }
            }

            if (!fitted)
            {
                Point newPos = new Point(matrix.size.y - word.Length, matrix.size.x - 1);
                if (matrix.willVFit(word, newPos))
                {
                    matrix.setVWord(word, newPos);
                    Util.add(lineSolve(words, allowDiagonal, matrix), mxs);
                    fitted = true;
                }
            }

            if (!fitted)
            {
                Util.add(matrix, mxs);
            }

            return mxs;
        }

        public List<Matrix> FitVertical(List<string> words, string word, bool allowDiagonal, Matrix matrix)
        {
            List<Matrix> mxs = new List<Matrix>();

            if (words.Count == 0)
            {
                //               matrix.print();
                found = true;
                Util.add(matrix, mxs);
                return mxs;
            }

            if (matrix.isEmpty)
            {
                Point newPos = new Point(0, 0);
                if (matrix.willVFit(word, newPos))
                {
                    matrix.setVWord(word, newPos);
                    Util.add(lineSolve(words, allowDiagonal, matrix), mxs);
                }
                return mxs;
            }

            bool fitted = false;
            for (int i = 0; i < word.Length && !found && !reachedLimit(); i++)
            {
                char c = word[i];
                Point pos = new Point(-1, -1);
                bool goWhile = true;
                while (!found && !reachedLimit() && goWhile)
                {
                    pos = matrix.findChar(c, new Point(pos.y + 1, pos.x + 1));
                    if (pos.y != int.MaxValue && pos.x != int.MaxValue)
                    {
                        if (pos.y - i < word.Length)
                        {
                            Matrix m = Util.copy(matrix);
                            Point newPos = new Point(pos.y - i, pos.x);
                            if (m.willVFit(word, newPos))
                            {
                                m.setVWord(word, newPos);
                                Util.add(lineSolve(words, allowDiagonal, m), mxs);
                                fitted = true;
                            }
                        }
                        else
                        {
                            Tuple<Point, Point> bounds = matrix.bounds();
                            Point tBounds = bounds.Item1;
                            Point bBounds = bounds.Item2;

                            Point top = new Point(tBounds.y, tBounds.x);
                            Point bottom = new Point(matrix.size.y - bBounds.y, matrix.size.y - bBounds.x);

                            if (top.y > i)
                            {
                                Matrix m = Util.copy(matrix);
                                m.move(new Point(-i, 0));
                                Point newPos = new Point(pos.y - i, pos.x);
                                if (m.willVFit(word, newPos))
                                {
                                    m.setVWord(word, newPos);
                                    Util.add(lineSolve(words, allowDiagonal, m), mxs);
                                    fitted = true;
                                }
                            }

                            if (bottom.y > word.Length - i)
                            {
                                Matrix m = Util.copy(matrix);
                                m.move(new Point(word.Length - i, 0));
                                Point newPos = new Point(pos.y + word.Length - i, pos.x);
                                if (m.willVFit(word, newPos))
                                {
                                    m.setVWord(word, newPos);
                                    Util.add(lineSolve(words, allowDiagonal, m), mxs);
                                    fitted = true;
                                }
                            }
                        }
                    }
                    else
                    {
                        goWhile = false;
                    }
                }
            }

            if (!fitted)
            {
                Point newPos = new Point(matrix.size.y - 1, matrix.size.x - word.Length);
                if (matrix.willHFit(word, newPos))
                {
                    matrix.setHWord(word, newPos);
                    Util.add(lineSolve(words, allowDiagonal, matrix), mxs);
                    fitted = true;
                }

            }

            if (!fitted)
            {
                Util.add(matrix, mxs);
            }

            return mxs;
        }

        public static Matrix FitFreeSpace(List<string> words, Matrix matrix)
        {
            for (int j = 0; j < 20; j++)
            {
                string word = "";

                if (words.Count == 0)
                {
                    return matrix;
                }
                else
                {
                    word = words[0];
                    words.RemoveAt(0);
                }

                for (int i = 0; i < matrix.size.x; i++)
                {
                    Point vPos = matrix.maxVSpace(i);
                    if (vPos.x >= word.Length)
                    {
                        matrix.setVWord(word, new Point(vPos.y, i));
                        break;
                    }
                }

                if (words.Count == 0)
                {
                    return matrix;
                }
                else
                {
                    word = words[0];
                    words.RemoveAt(0);
                }

                for (int i = 0; i < matrix.size.y; i++)
                {
                    Point hPos = matrix.maxHSpace(i);
                    if (hPos.y >= word.Length)
                    {
                        matrix.setHWord(word, new Point(i, hPos.x));
                        break;
                    }
                }
            }

            // Failed, return what we've got
            return matrix;
        }

    }
}
