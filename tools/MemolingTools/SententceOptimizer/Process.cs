using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SententceOptimizer
{
    class Process
    {
        public static int MaxSentences = 3;


        public List<Sentence> Sentences { get; set; }
        public Dictionary<Word, List<Sentence>> Data { get; set; }

        public Dictionary<Word, List<Sentence>> Unique { get ; set; }

        public void Read(string path)
        {
            Sentences = new List<Sentence>();
            Data = new Dictionary<Word, List<Sentence>>();
            Unique = new Dictionary<Word,List<Sentence>>();

            using (StreamReader sr = new StreamReader(path))
            {
                while (!sr.EndOfStream)
                {
                    Sentence sentence = new Sentence();
                    string line = sr.ReadLine();
                    string[] parts = line.Split('\t');
                    string lang = parts[1];
                    string[] words = parts[2].Replace(".", " ").Replace(",", " ").Replace(":", " ").
                        Replace("!", " ").Replace("'", "  ").Replace("\"", " ").Replace("¿", " ").
                        Replace("?", " ").Replace("¡", " ").Replace("!", " ").Replace(";", " ").ToLower()
                        .Split(' ');

                    if (lang == "cmn" || lang == "jpn")
                    {
                        // Ignore chinese - not easy to process
                        continue;
                    }

                    sentence.Id = int.Parse(parts[0]);
                    sentence.Line = line;
                    sentence.Words = words.Select(s => new Word(lang, s)).ToArray();
                    Sentences.Add(sentence);

                    foreach (var w in sentence.Words)
                    {
                        if (!Data.Keys.Contains(w))
                        {
                            Data.Add(w, new List<Sentence>() { sentence });
                        }
                        else
                        {
                            Data[w].Add(sentence);
                        }
                    }
                }
            }

        }

        public void FineRefine()
        {
            int i = 3;

            while (true)
            {
                int deleted = Refine(i++);
                if (deleted == 0)
                {
                    break;
                }
            }

        }

        public int Refine(int threshold)
        {
            // Remove ones that fulfil criteria
            List<Word> toDelete = new List<Word>();
            foreach(var val in Data) {
                if(val.Value.Count <= threshold) {
                    Unique.Add(val.Key,val.Value);
                    toDelete.Add(val.Key);
                }
            }

            for (int i = 0; i < toDelete.Count;i++) 
            {
                var val = toDelete[i];
                var sents = Data[val].ToList();
                foreach (var sen in sents)
                {
                    foreach (var word in sen.Words)
                    {
                        Data[word].Remove(sen);
                    }
                }
                Data.Remove(val);
            }

            return toDelete.Count;
        }

        public void Write(string path)
        {
            HashSet<Sentence> uniqueSentences = new HashSet<Sentence>();

            foreach (var u in Unique)
            {
                foreach(var s in u.Value) {
                    if (!uniqueSentences.Contains(s))
                    {
                        uniqueSentences.Add(s);
                    }
                }
            }

            using (StreamWriter sw = new StreamWriter(path))
            {
                foreach (var unique in uniqueSentences)
                {
                    sw.WriteLine(string.Format("%s\t%s", unique.Words[0].Language, unique.Line));
                }

            }

        }
    }
}
