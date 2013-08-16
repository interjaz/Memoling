using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TatoebaWordLookup
{
    class Program
    {

        static void Main(string[] args)
        {

            // language, letter, words, sentences
            Dictionary<string, Dictionary<char, Dictionary<string, List<int>>>> data = new Dictionary<string, Dictionary<char, Dictionary<string, List<int>>>>();

            using (StreamReader sr = new StreamReader(@"C:\tmp\sentences.csv"))
            {
                while (!sr.EndOfStream)
                {
                    string line = sr.ReadLine();
                    string[] parts = line.Split('\t');

                    int id = int.Parse(parts[0]);
                    string lang = parts[1];

                    string sentence = parts[2];
                   
                    string[] words = sentence.Replace(".", " ").Replace(",", " ").Replace(":", " ").
                   Replace("!", " ").Replace("'", "  ").Replace("\"", " ").Replace("¿", " ").
                   Replace("?", " ").Replace("¡", " ").Replace("!", " ").Replace(";", " ")
                   .Replace("<", " ").Replace(">", " ").Replace("-", " ")
                   .Replace("0", " ").Replace("1", " ").Replace("2", " ")
                   .Replace("3", " ").Replace("4", " ").Replace("5", " ")
                   .Replace("6", " ").Replace("7", " ").Replace("8", " ")
                   .Replace("9", " ").Replace("(", " ").Replace(")", " ")
                   .Replace("@", " ").Replace("#", " ").Replace("$", " ")
                   .Replace("%", " ").Replace("^", " ").Replace("&", " ")
                   .Replace("*", " ").Replace("_", " ").Replace("=", " ")
                   .Replace("~", " ").Replace("`", " ").Replace("{", " ")
                   .Replace("}", " ").Replace("[", " ").Replace("]", " ")
                   .Replace(":", " ").Replace(";", " ").Replace("'", " ")
                   .Replace("\"", " ").Replace("<", " ").Replace(">", " ")
                   .Replace("?", " ").Replace("/", " ").ToLower()
                   .Split(new char[] {' '}, StringSplitOptions.RemoveEmptyEntries);

                    Dictionary<char, Dictionary<string, List<int>>> spec;
                    if (data.Keys.Contains(lang))
                    {
                        spec = data[lang];
                    }
                    else
                    {
                        spec = new Dictionary<char, Dictionary<string, List<int>>>();
                        data.Add(lang, spec);
                    }

                    foreach (string word in words)
                    {
                        Dictionary<string, List<int>> deep;
                        char letter = word[0];

                        // move to unstandarized ones
                        if (letter < 'a' || letter > 'z')
                        {
                            letter = '_';
                        }

                        if (spec.Keys.Contains(letter))
                        {
                            deep = spec[letter];
                        }
                        else
                        {
                            deep = new Dictionary<string, List<int>>();
                            spec.Add(letter, deep);
                        }

                        if (deep.Keys.Contains(word))
                        {
                            deep[word].Add(id);
                        }
                        else
                        {
                            deep.Add(word, new List<int> { id });
                        }
                    }

                }
            }

            // Represent in disk structure
            string root = @"C:\tmp\tatoeba_tree";

            DirectoryInfo dir = new DirectoryInfo(root);

            if (!dir.Exists)
            {
                dir.Create();
            }
            else
            {
                dir.Delete(true);
                dir.Create();
            }

            foreach (var lang in data)
            {

                if (lang.Key.Length == 0 || lang.Key[0] < 'a' || lang.Key[0] > 'z')
                {
                    // something wrong skip it
                    continue;
                }

                var langDir = dir.CreateSubdirectory(lang.Key);

                foreach (var letter in lang.Value)
                {
                    var letterDir = langDir.CreateSubdirectory(letter.Key.ToString());

                    using (var sw = new StreamWriter(letterDir.FullName + "/word.csv"))
                    {
                        foreach (var word in letter.Value)
                        {
                            foreach (var line in word.Value)
                            {
                                sw.WriteLine(string.Format("{0}\t{1}", word.Key, line));
                            }
                        }
                    }
                }

            }

            int a = 2;
            a++;
        }
    }
}
