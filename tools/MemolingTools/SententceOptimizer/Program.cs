using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SententceOptimizer
{
    class Program
    {
        static void Main(string[] args)
        {
            string file_in = @"C:\tmp\sentences.csv";
            string file_out = @"C:\tmp\sentences.min.csv";

            Process p = new Process();
            p.Read(file_in);
            p.FineRefine();
            p.Write(file_out);


            System.Diagnostics.Debug.Write("read");

        }

        
    }
}
