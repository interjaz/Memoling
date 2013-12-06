using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace RenameFiles
{
    class Program
    {
        static void Main(string[] args)
        {
            DirectoryInfo from = new DirectoryInfo(@"C:\tmp\res");
            Rename(from);
        }

        static void Rename(DirectoryInfo dir)
        {
            foreach(var d in dir.GetDirectories()) 
            {
                Rename(d);
            }

            foreach (var file in dir.GetFiles())
            {
                if (file.Name.StartsWith("_"))
                {
                    File.Move(file.FullName, file.Directory.FullName + @"\" + file.Name.Substring(1));
                }
            }
        }
    }
}
