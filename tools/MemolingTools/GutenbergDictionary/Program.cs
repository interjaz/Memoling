using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace MemolingTools.GutenbergDictionary
{
    class Program
    {
        static void Main(string[] args)
        {

            string from = args[0];
            string to = args[1];

            string[] files = Directory.GetFiles(from);


            long length = 0;
            List<StreamReader> srs = new List<StreamReader>();

            foreach (string name in files)
            {
                FileInfo file = new FileInfo(name);
                length += file.Length;
                srs.Add(new StreamReader(name));
            }



            using (StreamWriter sw = new StreamWriter(to))
            {
                Seeker.Collect(srs, sw, length);
            }


            foreach (var s in srs) s.Close();

        }
    }
}
