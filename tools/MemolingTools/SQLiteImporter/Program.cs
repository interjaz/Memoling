using System;
using System.Collections.Generic;
using System.Data.SQLite;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MemolingTools.SQLiteImporter
{
    class Program
    {

        static void Main(string[] args)
        {
            LanguageInsert("2of12", "EN");
            LanguageInsert("fr", "FR");
            LanguageInsert("ge", "DE");
            LanguageInsert("it", "IT");
            LanguageInsert("pl", "PL");
            LanguageInsert("ru", "RU");
            LanguageInsert("sp", "SPA");


        }

        private static void LanguageInsert(string name, string iso)
        {

            int i = 0;
            SqlWrapper wrapper = new SqlWrapper();


            FileInfo dictionary = new FileInfo(@"C:\Users\Bartosz\Documents\memoling-dictionaries\" + name + ".txt");
            FileInfo db = new FileInfo(@"C:\Users\Bartosz\Documents\eclipse\Memoling\assets\TranslateMemo.sqlite");

            wrapper.Connect(db.FullName);


            using (StreamReader sr = new StreamReader(dictionary.FullName))
            {
                using (SQLiteTransaction trans = wrapper.connection.BeginTransaction())
                {

                    while (!sr.EndOfStream)
                    {
                        string line = sr.ReadLine();
                        string word = line.Split(';')[0];
                        if (word.Length < 3)
                            continue;

                        wrapper.ExecuteNonQuery(
                            "INSERT INTO 'WordLists' VALUES (@Word, @Language)",
                            trans,
                            new Tuple<string, string>("@Word", word),
                            new Tuple<string, string>("@Language", iso)
                        );
                        i += line.Length;

                        if (i % 1000 == 0)
                            Console.WriteLine((i * 100.0 / dictionary.Length).ToString("0.00"));
                    }

                    trans.Commit();
                }
            }

            wrapper.Close();
        }
    }
}
