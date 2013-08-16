using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TatoebaLanguageDiff
{
    class Program
    {
        static void Main(string[] args)
        {

            System.Diagnostics.Debug.WriteLine("-------------------------------------------------------");
            List<string> shown = new List<string>();
            using(StreamReader f8 = new StreamReader(@"C:\tmp\f18.csv"))
            using (StreamReader f3 = new StreamReader(@"C:\tmp\f3.csv"))
            using (StreamReader or = new StreamReader(@"C:\tmp\sentences.csv"))
            {
                while (!or.EndOfStream)
                {
                    string or_lang = or.ReadLine().Split('\t')[1];
                    string f3_lang = f3.ReadLine().Split('\t')[1];
                    string f8_lang = f8.ReadLine().Split('\t')[1];

                    if (f3_lang != f8_lang && !shown.Contains(or_lang))
                    {
                        System.Diagnostics.Debug.WriteLine(string.Format("{0}\t{1}\t{2}", or_lang, f3_lang, f8_lang));
                        shown.Add(or_lang);
                    }
                }
            }
        }
    }
}
