using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TatoebaLanguageInteger
{
    class Program
    {
        static void Main(string[] args)
        {

            Dictionary<string, int> lang = new Dictionary<string, int>();
            int i = 1;
            using (StreamWriter sw = new StreamWriter(@"C:\tmp\sentences.langcs.csv"))
            {
                using (StreamReader sr = new StreamReader(@"C:\tmp\sentences.csv"))
                {
                    while (!sr.EndOfStream)
                    {
                        string line = sr.ReadLine();
                        string[] parts = line.Split('\t');

                        int num = 1;
                        if (!lang.Keys.Contains(parts[1]))
                        {
                            num = i;
                            lang.Add(parts[1], i++);
                        }
                        else
                        {
                            num = lang[parts[1]];
                        }

                        sw.Write(string.Format("{0}\t{1}\t{2}\n", parts[0], num, parts[2]));
                    }
                }
            }

            /*
            using(StreamWriter sw = new StreamWriter(@"C:\tmp\LanguageTranslation.php")) 
            {
                sw.WriteLine("<?php");

                sw.WriteLine("class LanguageTranslation {");

                sw.WriteLine("public function languageIso6393ToInt($language) {");
                sw.WriteLine("\tswitch($language) {");

                foreach (var d in lang)
                {
                    var l = d.Key;
                    var ret = d.Value;
                    sw.WriteLine("\t\tcase '" + l + "':");
                    sw.WriteLine("\t\t\t return " + ret + ";");
                    sw.WriteLine("\t\tbreak;");
                }

                sw.WriteLine("\t}");

                sw.WriteLine("public function languageIntToIso6393($int) {");
                sw.WriteLine("\tswitch($int) {");

                foreach (var d in lang)
                {
                    var l = d.Key;
                    var ret = d.Value;
                    sw.WriteLine("\t\tcase " + ret + ":");
                    sw.WriteLine("\t\t\t return '" + l + "';");
                    sw.WriteLine("\t\tbreak;");
                }

                sw.WriteLine("\t}");

                sw.WriteLine("}");

                sw.WriteLine("?>");

            }
            */
        }
    }
}
