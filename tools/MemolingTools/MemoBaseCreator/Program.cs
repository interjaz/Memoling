using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MemoBaseCreator
{
    class Program
    {
        static void Main(string[] args)
        {
            string fileIn = @"C:\Users\Bartosz\Desktop\words to add\fce-pl.csv";
            string fileOut = fileIn + ".sql";
            string name = "FCE (PL)";
            string description = "Set of words for FCE with translations in Polish";
            string langA = "EN";
            string langB = "FR";

            using(StreamWriter sw = new StreamWriter(fileOut))
            using (StreamReader sr = new StreamReader(fileIn))
            {
                sw.WriteLine(@"START TRANSACTION;");

                string memoBaseId = Guid.NewGuid().ToString();
                string db = "jupiterdb";

                string createMemoBase = @"INSERT INTO `" + db + @"`.`memoling_MemoBases`
(`MemoBaseId`,`Name`,`Created`,`Active`)
VALUES
(
'" + memoBaseId + @"',
'" + name + @"',
'" + DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss") + @"',
1
);";
                sw.WriteLine(createMemoBase);
                sw.WriteLine();

                string published = @"INSERT INTO `" + db + @"`.`memoling_PublishedMemoBases`
(`PublishedMemoBaseId`,`FacebookUserId`,`MemoBaseId`,`MemoBaseGenreId`,`Description`,`Downloads`,`AdminsScore`,`UsersScore`,`Created`,`PrimaryLanguageAIso639`,`PrimaryLanguageBIso639`)
VALUES
(
'" + Guid.NewGuid().ToString() + @"',
100002530762250,
'" + memoBaseId + @"',
'5aaf1226-754e-4f5b-8212-4520b597ab93',
'" + description + @"',
0,
0,
0,
'" + DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss") + @"',
'" + langA + @"',
'" + langB + @"'
);";

                sw.WriteLine(published);
                sw.WriteLine();

                while (!sr.EndOfStream)
                {
                    string line = sr.ReadLine();

                    string[] parts = line.Split('\t');
                    string fromLang = parts[0];
                    string from = parts[1];
                    string fromClass = parts[2];
                    string toLang = parts[3];
                    string to = parts[4];
                    string fromDesc = parts[5];
                    string fromPhon = "";

                    if (parts.Length > 6)
                    {
                        fromPhon = parts[6];
                    }

                    // Insert from word
                    string wordAId = Guid.NewGuid().ToString();
                    string wordA = @"INSERT INTO `" + db + @"`.`memoling_Words`
(`WordId`,`LanguageIso639`,`Word`,`Description`,`Class`,`Phonetic`)
VALUES
(
'" + wordAId + @"',
'" + fromLang + @"',
'" + from.Replace("'", "\\'") + @"',
'" + fromDesc.Replace("'", "\\'") + @"',
'" + fromClass + @"',
'" + fromPhon.Replace("'", "\\'") + @"'
);";
                    sw.WriteLine(wordA);
                    sw.WriteLine();

                    // Insert to word
                    string wordBId = Guid.NewGuid().ToString();
                    string wordB = @"INSERT INTO `" + db + @"`.`memoling_Words`
(`WordId`,`LanguageIso639`,`Word`,`Description`,`Class`,`Phonetic`)
VALUES
(
'" + wordBId + @"',
'" + toLang + @"',
'" + to.Replace("'", "\\'") + @"',
'',
'',
''
);";
                    sw.WriteLine(wordB);
                    sw.WriteLine();
                    // Insert memo
                    string memo = "INSERT INTO `" + db + @"`.`memoling_Memos`
(`MemoId`,`MemoBaseId`,`WordAId`,`WordBId`,`Created`,`LastReviewed`,`Displayed`,`CorrectAnsweredWordA`,`CorrectAnsweredWordB`,`Active`)
VALUES
(
'" + Guid.NewGuid().ToString() + @"',
'" + memoBaseId + @"',
'" + wordAId + @"',
'" + wordBId + @"',
'" + DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss") + @"',
'" + DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss") + @"',
0,
0,
0,
1
);";
                    sw.WriteLine(memo);
                    sw.WriteLine();


                }
                sw.WriteLine("COMMIT;");
            }

        }

    }
}
