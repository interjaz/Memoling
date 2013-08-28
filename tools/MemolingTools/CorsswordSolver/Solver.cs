using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CorsswordSolver
{
    class Solver
    {
        private bool found = false;
        private bool allowDiagonal = false;

        private int operations = 0;
        private int maxOperations = 5000;

        private Random r = new Random();

        public bool success()
        {
            return found;
        }

        private bool stop()
        {
            operations++;
            return found || operations > maxOperations;
        }

        public static Matrix dualSolve(List<string> words, bool allowDiagonal, Matrix matrix)
        {
            words = words.OrderByDescending(o => o.Length).ToList();
            Solver s1 = new Solver();
            matrix = s1.solve(words, true, 5000, matrix);

            words = Util.diff(matrix.words, words);
            words = words.OrderBy(o => o.Length).ToList();
            Solver s2 = new Solver();
            matrix = s2.solve(words, false, 1000, matrix);

            if (!s2.success())
            {
                words = Util.diff(matrix.words, words);
                matrix = Solver.FitFreeSpace(words, matrix);
            }

            return matrix;
        }

        public Matrix solve(List<string> words, bool allowDiagonal, int maxOperations, Matrix matrix)
        {
            this.allowDiagonal = allowDiagonal;
            this.maxOperations = maxOperations;
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

                List<Matrix> lms = lineSolve(permWords, matrix, true);
                if (lms != null)
                {
                    for (int i = 0; i < lms.Count; i++)
                    {
                        ms.Add(lms[i]);
                    }
                }

            } while (!stop() && Util.nextPermutation(perm));

            Matrix last = ms.OrderBy(o => o.words.Count).LastOrDefault();

            return last;
        }

        public List<Matrix> lineSolve(List<string> words, Matrix matrix, bool forward)
        {
            List<Matrix> mxs = new List<Matrix>();

            if (stop() || words == null || words.Count == 0)
            {
                found = true;
                mxs.Add(matrix);
                return mxs;
            }

            for (int i = 0; i < words.Count; i++)
            {
                string word = words[i];
                List<string> vWords = Util.copy(words);
                List<string> hWords = Util.copy(words);
                List<string> dlWords = Util.copy(words);
                List<string> drWords = Util.copy(words);
                vWords.RemoveAt(i); vWords = vWords.Count == 0 ? null : vWords;
                hWords.RemoveAt(i); hWords = hWords.Count == 0 ? null : hWords;
                dlWords.RemoveAt(i); dlWords = dlWords.Count == 0 ? null : dlWords;
                drWords.RemoveAt(i); drWords = drWords.Count == 0 ? null : drWords;

                List<Matrix> ms;
                bool fwd;
                // Tunning parameter it seems that leaving it as true gives better result
                //fwd = !forward;
                fwd = true;

                if (allowDiagonal)
                {

                    ms = FitLDiagonal(dlWords, word, Util.copy(matrix), fwd);
                    if (ms != null)
                    {
                        Util.add(ms, mxs);
                    }

                    if (stop())
                    {
                        return mxs;
                    }

                    ms = FitRDiagonal(drWords, word, Util.copy(matrix), fwd);
                    if (ms != null)
                    {
                        Util.add(ms, mxs);
                    }

                    if (stop())
                    {
                        return mxs;
                    }

                }

                ms = FitHorizontal(vWords, word, Util.copy(matrix), fwd);
                if (ms != null)
                {
                    Util.add(ms, mxs);
                }

                if (stop())
                {
                    return mxs;
                }

                ms = FitVertical(hWords, word, Util.copy(matrix), fwd);
                if (ms != null)
                {
                    Util.add(ms, mxs);
                }

                if (stop())
                {
                    return mxs;
                }

            }

            return mxs;
        }

        public List<Matrix> FitHorizontal(List<string> words, string word, Matrix matrix, bool forward)
        {
            List<Matrix> mxs = new List<Matrix>();

            if (matrix.isEmpty)
            {
                Point newPos = new Point(0, 0);
                if (matrix.willHFit(word, newPos))
                {
                    matrix.setHWord(word, new Point(0, 0));
                    Util.add(lineSolve(words, matrix, forward), mxs);
                }
                return mxs;
            }

            bool fitted = false;

            for (int j = 0; j < matrix.size.y && !stop(); j++)
            {
                for (int index = 0; index < word.Length && !stop(); index++)
                {
                    int i = forward ? index : word.Length - 1 - index;
                    // letters before
                    int _i = i;
                    // letters after
                    int i_ = word.Length - i - 1;

                    char c = word[i];
                    Point pos = new Point(j, -1);
                    pos = matrix.findChar(c, new Point(pos.y, pos.x + 1));
                    if (!pos.isVaild())
                    {
                        continue;
                    }

                    if (pos.x - i >= 0 && pos.x + i_ < matrix.size.x)
                    {
                        Matrix m = Util.copy(matrix);
                        Point newPos = new Point(pos.y, pos.x - i);
                        if (m.willHFit(word, newPos))
                        {
                            m.setHWord(word, newPos);
                            Util.add(lineSolve(words, m, forward), mxs);
                            fitted = true;
                        }
                    }
                    else
                    {
                        Tuple<Point, Point> bounds = matrix.bounds();
                        Point tBounds = bounds.Item1;
                        Point bBounds = bounds.Item2;

                        Point top = new Point(tBounds.y, tBounds.x);
                        Point bottom = new Point(matrix.size.y - bBounds.y - 1, matrix.size.y - bBounds.x - 1);

                        if (top.x > i)
                        {
                            // Move up
                            Matrix m = Util.copy(matrix);
                            int gap = matrix.size.x - pos.x - 1;
                            int mov = -(i_ - gap);
                            mov = mov < 0 ? mov : 0;
                            m.move(new Point(0, mov));
                            Point newPos = new Point(pos.y, pos.x + mov - i);
                            if (m.willHFit(word, newPos))
                            {
                                m.setHWord(word, newPos);
                                Util.add(lineSolve(words, m, forward), mxs);
                                fitted = true;
                            }
                        }

                        if (bottom.x > i)
                        {
                            // Move down
                            Matrix m = Util.copy(matrix);
                            int gap = pos.x;
                            int mov = i - gap;
                            mov = mov > 0 ? mov : 0;
                            // move so first letter is on x=0 position
                            m.move(new Point(0, mov));
                            Point newPos = new Point(pos.y, 0);
                            if (m.willHFit(word, newPos))
                            {
                                m.setHWord(word, newPos);
                                Util.add(lineSolve(words, m, forward), mxs);
                                fitted = true;
                            }
                        }
                    }

                }
            }

            if (!fitted)
            {
                Util.add(matrix, mxs);
            }

            if (fitted && words == null)
            {
                found = true;
            }

            return mxs;
        }

        public List<Matrix> FitVertical(List<string> words, string word, Matrix matrix, bool forward)
        {
            List<Matrix> mxs = new List<Matrix>();

            if (matrix.isEmpty)
            {
                Point newPos = new Point(0, 0);
                if (matrix.willVFit(word, newPos))
                {
                    matrix.setVWord(word, newPos);
                    Util.add(lineSolve(words, matrix, forward), mxs);
                }
                return mxs;
            }

            bool fitted = false;

            for (int j = 0; j < matrix.size.x && !stop(); j++)
            {
                for (int index = 0; index < word.Length && !stop(); index++)
                {
                    int i = forward ? index : word.Length - 1 - index;
                    // letters before
                    int _i = i;
                    // letters after
                    int i_ = word.Length - i - 1;

                    char c = word[i];
                    Point pos = new Point(-1, j);
                    pos = matrix.findChar(c, new Point(pos.y + 1, pos.x));
                    if (!pos.isVaild())
                    {
                        continue;
                    }

                    if (pos.y - i >= 0 && pos.y + i_ < matrix.size.y)
                    {
                        Matrix m = Util.copy(matrix);
                        Point newPos = new Point(pos.y - i, pos.x);
                        if (m.willVFit(word, newPos))
                        {
                            m.setVWord(word, newPos);
                            Util.add(lineSolve(words, m, forward), mxs);
                            fitted = true;
                        }
                    }
                    else
                    {
                        Tuple<Point, Point> bounds = matrix.bounds();
                        Point tBounds = bounds.Item1;
                        Point bBounds = bounds.Item2;

                        Point top = new Point(tBounds.y, tBounds.x);
                        Point bottom = new Point(matrix.size.y - bBounds.y - 1, matrix.size.y - bBounds.x - 1);

                        if (top.y > i)
                        {
                            // Move up
                            Matrix m = Util.copy(matrix);
                            int gap = matrix.size.y - pos.y - 1;
                            int mov = -(i_ - gap);
                            mov = mov < 0 ? mov : 0;
                            m.move(new Point(mov, 0));
                            Point newPos = new Point(pos.y + mov - i, pos.x);
                            if (m.willVFit(word, newPos))
                            {
                                m.setVWord(word, newPos);
                                Util.add(lineSolve(words, m, forward), mxs);
                                fitted = true;
                            }
                        }

                        if (bottom.y > i)
                        {
                            // Move down
                            Matrix m = Util.copy(matrix);
                            int gap = pos.y;
                            int mov = i - gap;
                            mov = mov > 0 ? mov : 0;
                            // move so first letter is on y=0 position
                            m.move(new Point(mov, 0));
                            Point newPos = new Point(0, pos.x);
                            if (m.willVFit(word, newPos))
                            {
                                m.setVWord(word, newPos);
                                Util.add(lineSolve(words, m, forward), mxs);
                                fitted = true;
                            }
                        }
                    }
                }
            }

            if (fitted && words == null)
            {
                found = true;
            }

            if (!fitted)
            {
                Util.add(matrix, mxs);
            }

            return mxs;
        }

        public List<Matrix> FitLDiagonal(List<string> words, string word, Matrix matrix, bool forward)
        {
            List<Matrix> mxs = new List<Matrix>();
            
            if (matrix.isEmpty)
            {
                Point newPos = new Point(0, 0);
                if (matrix.willLDFit(word, newPos))
                {
                    matrix.setLDWord(word, newPos);
                    Util.add(lineSolve(words, matrix, forward), mxs);
                }
                return mxs;
            }

            bool fitted = false;

            for (int j = 0; j < matrix.size.x && !stop(); j++)
            {
                for (int index = 0; index < word.Length && !stop(); index++)
                {
                    int i = forward ? index : word.Length - 1 - index;
                    // letters before
                    int _i = i;
                    // letters after
                    int i_ = word.Length - i - 1;

                    char c = word[i];
                    Point pos = new Point(-1, j);
                    pos = matrix.findChar(c, new Point(pos.y+1, pos.x));
                    if (!pos.isVaild())
                    {
                        continue;
                    }

                    Tuple<Point, Point> bounds = matrix.bounds();
                    Point tBounds = bounds.Item1;
                    Point bBounds = bounds.Item2;

                    Point top = new Point(tBounds.y, tBounds.x);
                    Point bottom = new Point(matrix.size.y - bBounds.y - 1, matrix.size.y - bBounds.x - 1);

                    Matrix m = null;
                    int gapX = 0;
                    int gapY = 0;
                    int movX = 0;
                    int movY = 0;

                    // Fits without moving
                    if (pos.y >= i && pos.y + i_ < matrix.size.y &&
                        pos.x >= i && pos.x + i_ < matrix.size.x)
                    {
                        m = Util.copy(matrix);
                        movX = 0;
                        movY = 0;
                    }
                    // Move left
                    else if (top.x >= i_ && pos.x >= i && pos.x + i < matrix.size.x)
                    {

                        // Stay
                        if (pos.y >= i && pos.y + i_ < matrix.size.y)
                        {
                            m = Util.copy(matrix);
                            gapX = matrix.size.x - pos.x - 1;
                            gapY = matrix.size.y - pos.y - 1;
                            movX = -(i_ - gapX);
                            movY = 0;
                            m.move(new Point(movY, movX));
                        }
                        // Move up
                        else if (pos.y + i_ >= matrix.size.y && top.y > i)
                        {
                            m = Util.copy(matrix);
                            gapX = matrix.size.x - pos.x - 1;
                            gapY = matrix.size.y - pos.y - 1;
                            movX = -(i_ - gapX);
                            movY = -(i_ - gapY);
                            movX = movX < 0 ? movX : 0;
                            movY = movY < 0 ? movY : 0;
                            m.move(new Point(movY, movX));
                        }
                        // Move down
                        else if (pos.y <= i && bottom.y > i_)
                        {
                            m = Util.copy(matrix);
                            gapX = matrix.size.x - pos.x - 1;
                            gapY = pos.y;
                            movX = -(i_ - gapX);
                            movY = i - gapY;
                            movX = movX < 0 ? movX : 0;
                            movY = movY > 0 ? movY : 0;
                            m.move(new Point(movY, movX));
                        }
                    }
                    // Move right
                    else if (bottom.x >= i && pos.x >= i_ && pos.x + i_ < matrix.size.x)
                    {
                        // Stay
                        if (pos.y >= i && pos.y + i_ < matrix.size.y)
                        {
                            m = Util.copy(matrix);
                            gapX = pos.x;
                            movX = i - gapX;
                            movX = movX > 0 ? movX : 0;
                            movY = 0;
                            m.move(new Point(movY, movX));
                        }
                        // Move up
                        else if (pos.y + i_ >= matrix.size.y && top.y > i)
                        {
                            m = Util.copy(matrix);
                            gapX = pos.x;
                            gapY = matrix.size.y - pos.y - 1;
                            movX = i - gapX;
                            movY = -(i_ - gapY);
                            movX = movX > 0 ? movX : 0;
                            movY = movY < 0 ? movY : 0;
                            m.move(new Point(movY, movX));
                        }
                        // Move down
                        else if (pos.y <= i && bottom.y > i_)
                        {
                            m = Util.copy(matrix);
                            gapX = pos.x;
                            gapY = pos.y;
                            movX = i - gapX;
                            movY = i - gapY;
                            movX = movX > 0 ? movX : 0;
                            movY = movY > 0 ? movY : 0;
                            m.move(new Point(movY, movX));
                        }
                    }

                    if (m != null)
                    {
                        Point newPos = new Point(pos.y + movY - i, pos.x + movX - i);
                        if (m.willLDFit(word, newPos))
                        {
                            m.setLDWord(word, newPos);
                            Util.add(lineSolve(words, m, forward), mxs);
                            fitted = true;
                        }
                    }
                        
                }
            }

            if (!fitted)
            {
                Util.add(matrix, mxs);
            }

            return mxs;
        }
        
        public List<Matrix> FitRDiagonal(List<string> words, string word, Matrix matrix, bool forward)
        {
            List<Matrix> mxs = new List<Matrix>();
            
            if (matrix.isEmpty)
            {
                Point newPos = new Point(0, 0);
                if (matrix.willRDFit(word, newPos))
                {
                    matrix.setRDWord(word, newPos);
                    Util.add(lineSolve(words, matrix, forward), mxs);
                }
                return mxs;
            }

            bool fitted = false;

            for (int j = 0; j < matrix.size.x && !stop(); j++)
            {
                for (int index = 0; index < word.Length && !stop(); index++)
                {
                    int i = forward ? index : word.Length - 1 - index;
                    // letters before
                    int _i = i;
                    // letters after
                    int i_ = word.Length - i - 1;

                    char c = word[i];
                    Point pos = new Point(-1, j);
                    pos = matrix.findChar(c, new Point(pos.y + 1, pos.x));
                    if (!pos.isVaild())
                    {
                        continue;
                    }

                    Tuple<Point, Point> bounds = matrix.bounds();
                    Point tBounds = bounds.Item1;
                    Point bBounds = bounds.Item2;

                    Point top = new Point(tBounds.y, tBounds.x);
                    Point bottom = new Point(matrix.size.y - bBounds.y - 1, matrix.size.y - bBounds.x - 1);

                    Matrix m = null;
                    int gapX = 0;
                    int gapY = 0;
                    int movX = 0;
                    int movY = 0;

                    // Fits without moving
                    if (pos.y >= i && pos.y + i_ < matrix.size.y &&
                        pos.x >= i_ && pos.x + i < matrix.size.x)
                    {
                        m = Util.copy(matrix);
                        movX = 0;
                        movY = 0;
                    }
                    // Move left
                    else if (top.x >= i && pos.x >= i_ && pos.x + i_ < matrix.size.x)
                    {

                        // Stay
                        if (pos.y >= i && pos.y + i_ < matrix.size.y)
                        {
                            m = Util.copy(matrix);
                            gapX = matrix.size.x - pos.x - 1;
                            gapY = matrix.size.y - pos.y - 1;
                            movX = -(i - gapX);
                            movY = 0;
                            m.move(new Point(movY, movX));
                        }
                        // Move up
                        else if (pos.y + i_ >= matrix.size.y && top.y > i)
                        {
                            m = Util.copy(matrix);
                            gapX = matrix.size.x - pos.x - 1;
                            gapY = matrix.size.y - pos.y - 1;
                            movX = -(i_ - gapX);
                            movY = -(i_ - gapY);
                            movX = movX < 0 ? movX : 0;
                            movY = movY < 0 ? movY : 0;
                            m.move(new Point(movY, movX));
                        }
                        // Move down
                        else if (pos.y <= i && bottom.y > i_)
                        {
                            m = Util.copy(matrix);
                            gapX = matrix.size.x - pos.x - 1;
                            gapY = pos.y;
                            movX = -(i - gapX);
                            movY = i - gapY;
                            movX = movX < 0 ? movX : 0;
                            movY = movY > 0 ? movY : 0;
                            m.move(new Point(movY, movX));
                        }
                    }
                    // Move right
                    else if (bottom.x >= i_ && pos.x >= i && pos.x + i < matrix.size.x)
                    {
                        // Stay
                        if (pos.y >= i && pos.y + i_ < matrix.size.y)
                        {
                            m = Util.copy(matrix);
                            gapX = pos.x;
                            movX = i_ - gapX;
                            movX = movX > 0 ? movX : 0;
                            movY = 0;
                            m.move(new Point(movY, movX));
                        }
                        // Move up
                        else if (pos.y + i_ >= matrix.size.y && top.y > i)
                        {
                            m = Util.copy(matrix);
                            gapX = pos.x;
                            gapY = matrix.size.y - pos.y - 1;
                            movX = i_ - gapX;
                            movY = -(i_ - gapY);
                            movX = movX > 0 ? movX : 0;
                            movY = movY < 0 ? movY : 0;
                            m.move(new Point(movY, movX));
                        }
                        // Move down
                        else if (pos.y <= i && bottom.y > i_)
                        {
                            m = Util.copy(matrix);
                            gapX = pos.x;
                            gapY = pos.y;
                            movX = i_ - gapX;
                            movY = i - gapY;
                            movX = movX > 0 ? movX : 0;
                            movY = movY > 0 ? movY : 0;
                            m.move(new Point(movY, movX));
                        }
                    }

                    if (m != null)
                    {
                        Point newPos = new Point(pos.y + movY - i, pos.x + movX + i);
                        if (m.willRDFit(word, newPos))
                        {
                            m.setRDWord(word, newPos);
                            Util.add(lineSolve(words, m, forward), mxs);
                            fitted = true;
                        }
                    }

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
                bool fitted = false;
                string word = "";

                if (words.Count == 0)
                {
                    return matrix;
                }
                else
                {
                    // take new any way
                    word = words[0];
                    words.RemoveAt(0);
                }

                for (int i = 0; i < matrix.size.x; i++)
                {
                    Point vPos = matrix.maxVSpace(i);
                    if (vPos.x >= word.Length)
                    {
                        matrix.setVWord(word, new Point(vPos.y, i));
                        fitted = true;
                        break;
                    }
                }

                if (words.Count == 0 && fitted)
                {
                    return matrix;
                }
                else if (fitted)
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

                if (words.Count == 0 && fitted)
                {
                    return matrix;
                }
            }

            // Failed, return what we've got
            return matrix;
        }

    }
}
