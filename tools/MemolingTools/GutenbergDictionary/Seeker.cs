using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace MemolingTools.GutenbergDictionary
{
    public class Seeker
    {
        public static Dictionary<string, int> allWords = new Dictionary<string, int>();
        public static string[] separator = new string[] { " ", ",", ".", "\"", "\t", "\r", "\n", "?", "!", "- ", " -", " - ", ";", "(", ")", "»", "«", "[", "]", "`", "*", "/", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "$", "´", "½", "—", "_", ":", "…", "„", "”", "{", "}", "=", "+", "^","%","$","#","~","`", "<", ">", "|", "\\", "/", "“", "¡", "¿" };

        public static void Collect(IList<StreamReader> srs, StreamWriter o, long maxLength) {

            int progress = 0;
            int displayed = 0;

            Console.WriteLine("00%");

            foreach (var i in srs)
            {
                while (!i.EndOfStream)
                {
                    string line = i.ReadLine();
                    line = line.Replace("--", "-");
                    string[] words = line.Split(separator, StringSplitOptions.RemoveEmptyEntries);

                    

                    foreach (string w in words)
                    {
                        string word = w;
                        while (word.Length > 0 && word[0] == '-')
                        {
                            word = word.Substring(1, word.Length - 1);
                        }

                        if (word.Length == 0)
                        {
                            continue;
                        }

                        if (allWords.Keys.Contains(word))
                        {
                            allWords[word]++;
                        }
                        else
                        {
                            allWords.Add(word, 1);
                        }
                    }

                    progress += line.Length;
                    double ratio = (double)progress / maxLength;

                    if (ratio > 0.8 && displayed < 7)
                    {
                        Console.WriteLine("80%");
                        displayed++;
                    }
                    else if (ratio > 0.6 && displayed < 6)
                    {
                        Console.WriteLine("60%");
                        displayed++;
                    }
                    else if (ratio > 0.4 && displayed < 5)
                    {
                        Console.WriteLine("40%");
                        displayed++;
                    }
                    else if (ratio > 0.2 && displayed < 4)
                    {
                        Console.WriteLine("20%");
                        displayed++;
                    }
                    else if (ratio > 0.1 && displayed < 3)
                    {
                        Console.WriteLine("10%");
                        displayed++;
                    }
                    else if (ratio > 0.05 && displayed < 2)
                    {
                        Console.WriteLine("05%");
                        displayed++;
                    }
                    else if (ratio > 0.01 && displayed < 1)
                    {
                        Console.WriteLine("01%");
                        displayed++;
                    }
                }

            }

            // Lower case words with only upper cases
            var toLower = allWords.Where(f => f.Key == f.Key.ToUpperInvariant()).ToList();

            foreach (var word in toLower)
            {
                string low = word.Key.ToLowerInvariant();
                string uF = upFirst(word.Key);

                if(allWords.Keys.Contains(low)) {
                    allWords[low] += word.Value;
                }
                else if (allWords.Keys.Contains(uF))
                {
                    allWords[uF] += word.Value;
                }
                else
                {
                    allWords.Add(low, word.Value);
                }

                allWords.Remove(word.Key);
                    
            }


            // If same word capital and lower case ignore uppercase
            var toDelete = allWords.Where(f => f.Key.Length > 0 && f.Key == upFirst(f.Key) && upFirst(f.Key) != lowFirst(f.Key) && allWords.Keys.Contains(lowFirst(f.Key))).ToList();

            foreach (var word in toDelete)
            {
                string low = lowFirst(word.Key);

                if (word.Value > allWords[low])
                {
                    allWords[word.Key] += allWords[low];
                    allWords.Remove(low);
                }
                else
                {
                    allWords[low] += word.Value;
                    allWords.Remove(word.Key);
                }
            }

            foreach (var word in allWords.OrderBy(k => k.Key))
            {
                o.WriteLine(string.Format("{0};{1}", word.Key, word.Value));
            }

            Console.WriteLine("100%");
        }

        private static string upFirst(string word)
        {
            if (word.Length == 1)
                return word.ToUpper();

            return word.Substring(0, 1).ToUpperInvariant() + word.Substring(1, word.Length-1);
        }

        private static string lowFirst(string word)
        {
            if (word.Length == 1)
                return word.ToLower();

            return word.Substring(0, 1).ToLowerInvariant() + word.Substring(1, word.Length - 1);
        }
    }
}
