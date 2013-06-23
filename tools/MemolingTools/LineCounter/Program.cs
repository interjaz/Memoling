using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace LineCounter
{
    class Program
    {

        static string[] exts;
        static DirectoryInfo root;

        static long[] lines;
        static long[] linesWs;

        static void Main(string[] args)
        {
            exts = args[0].Split(',');
            root = new DirectoryInfo(args[1]);
            lines = new long[exts.Length];
            linesWs = new long[exts.Length];

            Task.WaitAll(inspectPath(root).ToArray());

            long sum = 0;
            long sumWs = 0;
            for (int i = 0; i < exts.Length; i++)
            {
                sum += lines[i];
                sumWs += linesWs[i];

                Console.WriteLine(string.Format("{0,-10}:\t{1,-6}/ {2,-6}\t{3,-6}",
                    exts[i], lines[i], linesWs[i], Math.Round((1-((double)linesWs[i]-lines[i])/(linesWs[i]))*10000)/100.0
                    ));
            }

            Console.WriteLine(string.Format("Overall   :\t{0,-6}/ {1,-6}",
                sum, sumWs));

            Console.ReadLine();
        }

        static Random r = new Random();

        private static IEnumerable<Task> inspectPath(DirectoryInfo dir)
        {
            foreach (var d in dir.GetDirectories())
            {
                foreach (var t in inspectPath(d))
                {
                    yield return t;
                }
            }

            foreach (var f in dir.GetFiles())
            {
                yield return inspectFile(f);
            }
        }

        private static Task inspectFile(FileInfo file)
        {
            return Task.Factory.StartNew(() =>
            {
                int pos = Array.IndexOf(exts, file.Extension);
                if (pos == -1)
                {
                    return;
                }

                long fileLines = 0;
                long fileLinesWs = 0;

                using (StreamReader sr = new StreamReader(file.FullName))
                {
                    while (!sr.EndOfStream)
                    {
                        string line = sr.ReadLine();
                        if (!string.IsNullOrWhiteSpace(line))
                        {
                            fileLines++;
                        }
                        fileLinesWs++;
                    }
                }

                lock (lines)
                {
                    lines[pos] += fileLines;
                    linesWs[pos] += fileLinesWs;
                }
            });
        }
    }
}
