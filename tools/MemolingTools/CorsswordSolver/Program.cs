using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CorsswordSolver
{
    class Program
    {
        static void Main(string[] args)
        {
            example();
            //tests();
            Console.ReadKey();

        }

        static void example()
        {

            List<string> words = new List<string>() {
                "generating",
                "crossword",
                "magnitude",
                "improvements",
                "accessories",
                "residential",
                "have",
                "read",
                "about",
                "game",
                "using",
                "bottom",
                "edge",
                "seams",
                "help",
                "carto"
            };


            var m = Solver.dualSolve(words, true, new Matrix(new Point(12, 12)));
            m.print();

            Console.WriteLine(string.Format("{0}/{1}", m.words.Count, words.Count));
        }


        static void tests()
        {
            Matrix m = new Matrix(new Point(10, 10));

            //
            // Vertical tests
            //

            //Console.WriteLine("Test 1a");
            //m.setHWord("generating", new Point(0, 0));
            //foreach (var r in new Solver().FitVertical(new List<string>(), "read", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 1b");
            //foreach (var r in new Solver().FitVertical(new List<string>(), "read", Util.Copy(m), false))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 1c");
            //foreach (var r in new Solver().FitVertical(new List<string>(), "generatinh", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 1d");
            //foreach (var r in new Solver().FitVertical(new List<string>(), "generatinh", Util.Copy(m), false))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 2a");
            //m.setHWord("generatimg", new Point(0, 0));
            //m.setVWord("read", new Point(0, 4));
            //foreach (var r in new Solver().FitVertical(new List<string>(), "mug", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 2b");
            //foreach (var r in new Solver().FitVertical(new List<string>(), "mug", Util.Copy(m), false))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 3a");
            //m = new Matrix(new Point(10, 10));
            //m.setHWord("generating", new Point(9, 0));
            //foreach (var r in new Solver().FitVertical(new List<string>(), "read", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 3b");
            //foreach (var r in new Solver().FitVertical(new List<string>(), "read", Util.Copy(m), false))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 4a");
            //m = new Matrix(new Point(10, 10));
            //m.setHWord("generatimg", new Point(7, 0));
            //m.setVWord("read", new Point(6, 1));
            //foreach (var r in new Solver().FitVertical(new List<string>(), "mug", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 4b");
            //foreach (var r in new Solver().FitVertical(new List<string>(), "mug", Util.Copy(m), false))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 5a");
            //m = new Matrix(new Point(10, 10));
            //m.setHWord("generatimg", new Point(7, 0));
            //m.setVWord("read", new Point(6, 1));
            //foreach (var r in new Solver().FitVertical(new List<string>(), "xxxgxxx", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 5b");
            //m = new Matrix(new Point(10, 10));
            //m.setHWord("generatimg", new Point(1, 0));
            //m.setVWord("read", new Point(0, 1));
            //foreach (var r in new Solver().FitVertical(new List<string>(), "xxgxxx", Util.Copy(m), false))
            //{
            //    r.print();
            //}


            //
            // Horizontal tests
            //

            //Console.WriteLine("Test 1a");
            //m.setVWord("generating", new Point(0, 0));
            //foreach (var r in new Solver().FitHorizontal(new List<string>(), "read", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 1b");
            //foreach (var r in new Solver().FitHorizontal(new List<string>(), "read", Util.Copy(m), false))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 2a");
            //m.setVWord("generatimg", new Point(0, 0));
            //m.setHWord("read", new Point(4, 0));
            //foreach (var r in new Solver().FitHorizontal(new List<string>(), "mug", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 2b");
            //foreach (var r in new Solver().FitHorizontal(new List<string>(), "mug", Util.Copy(m), false))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 3a");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generating", new Point(0, 9));
            //foreach (var r in new Solver().FitHorizontal(new List<string>(), "read", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 3b");
            //foreach (var r in new Solver().FitHorizontal(new List<string>(), "read", Util.Copy(m), false))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 4a");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generatimg", new Point(0, 7));
            //m.setHWord("read", new Point(1, 6));
            //foreach (var r in new Solver().FitHorizontal(new List<string>(), "mug", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 4b");
            //foreach (var r in new Solver().FitHorizontal(new List<string>(), "mug", Util.Copy(m), false))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 5a");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generatimg", new Point(0, 7));
            //m.setHWord("read", new Point(1, 6));
            //foreach (var r in new Solver().FitHorizontal(new List<string>(), "xxxgxxx", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 5b");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generatimg", new Point(0, 1));
            //m.setHWord("read", new Point(1, 0));
            //foreach (var r in new Solver().FitHorizontal(new List<string>(), "xxgxxx", Util.Copy(m), false))
            //{
            //    r.print();
            //}

            //
            // Fit LD Test
            //

            //Console.WriteLine("Test 1a");
            //m.setVWord("generatind", new Point(0, 0));
            //foreach (var r in new Solver().FitLDiagonal(new List<string>(), "generhtind", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 1b");
            //foreach (var r in new Solver().FitLDiagonal(new List<string>(), "genejatind", Util.Copy(m), false))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 1c");
            //foreach (var r in new Solver().FitLDiagonal(new List<string>(), "xaad", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 1d");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generatind", new Point(0, 1));
            //m.setHWord("gee", new Point(1, 0));
            //foreach (var r in new Solver().FitLDiagonal(new List<string>(), "xxad", Util.Copy(m), true))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 2a");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generatind", new Point(0, 9));
            //foreach (var r in new Solver().FitLDiagonal(new List<string>(), "genejatind", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 2b");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generatind", new Point(0, 9));
            //foreach (var r in new Solver().FitLDiagonal(new List<string>(), "xxnxx", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 3c");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generatind", new Point(0, 1));
            //m.setHWord("eede", new Point(9, 0));
            //foreach (var r in new Solver().FitLDiagonal(new List<string>(), "xxnxx", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 3d");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generatind", new Point(0, 8));
            //m.setHWord("eede", new Point(9, 6));
            //foreach (var r in new Solver().FitLDiagonal(new List<string>(), "xxnxx", Util.Copy(m), true))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 4a");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("read", new Point(6, 9));
            //m.setHWord("j", new Point(8, 8));
            //foreach (var r in new Solver().FitLDiagonal(new List<string>(), "rxxxxxxxd", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("read", new Point(0, 9));
            //m.setHWord("j", new Point(1, 8));
            //foreach (var r in new Solver().FitLDiagonal(new List<string>(), "xxxxrxxxd", Util.Copy(m), true))
            //{
            //    r.print();
            //}


            //Console.WriteLine("Test 5a");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("read", new Point(6, 0));
            //m.setHWord("j", new Point(8, 1));
            //foreach (var r in new Solver().FitLDiagonal(new List<string>(), "rxxxxxxxd", Util.Copy(m), false))
            //{
            //    r.print();
            //}
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("read", new Point(0, 0));
            //foreach (var r in new Solver().FitLDiagonal(new List<string>(), "xxxxrxxx", Util.Copy(m), false))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 6a");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("read", new Point(6, 0));
            //foreach (var r in new Solver().FitLDiagonal(new List<string>(), "dx", Util.Copy(m), true))
            //{
            //    r.print();
            //}

            //
            // Fit RD Test
            //

            //Console.WriteLine("Test 1a");
            //m.setVWord("generatind", new Point(0, 9));
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "generhtind", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 1c");
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "xaad", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 1d");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generatind", new Point(0, 1));
            //m.setHWord("gee", new Point(1, 0));
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "xxad", Util.Copy(m), true))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 2a");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generatind", new Point(0, 9));
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "genejatind", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 2b");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generatind", new Point(0, 9));
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "xxnxx", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 3c");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generatind", new Point(0, 1));
            //m.setHWord("eede", new Point(9, 0));
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "xxnxx", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //Console.WriteLine("Test 3d");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("generatind", new Point(0, 8));
            //m.setHWord("eede", new Point(9, 6));
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "xxnxx", Util.Copy(m), true))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 4a");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("read", new Point(6, 8));
            //m.setHWord("j", new Point(8, 9));
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "rxxxx", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("read", new Point(0, 9));
            //m.setHWord("j", new Point(1, 8));
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "xxxxxd", Util.Copy(m), true))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 5a");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("read", new Point(0, 0));
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "rxxxx", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("read", new Point(6, 0));
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "rxxxx", Util.Copy(m), true))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 5a");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("read", new Point(0, 0));
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "xxxxr", Util.Copy(m), true))
            //{
            //    r.print();
            //}
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("read", new Point(0, 9));
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "xxxxdxxx", Util.Copy(m), true))
            //{
            //    r.print();
            //}

            //Console.WriteLine("Test 6a");
            //m = new Matrix(new Point(10, 10));
            //m.setVWord("read", new Point(6, 0));
            //foreach (var r in new Solver().FitRDiagonal(new List<string>(), "dx", Util.Copy(m), true))
            //{
            //    r.print();
            //}

            Console.WriteLine("Test 6a");
            m = new Matrix(new Point(12, 12));
            m.setLDWord("improvements", new Point(0, 0));
            m.setLDWord("generating", new Point(0, 2));
            m.setHWord("accersories", new Point(11, 1));
            foreach (var r in new Solver().FitRDiagonal(new List<string>(), "magnitude", Util.copy(m), true))
            {
                r.print();
            }

        }
    }
}
