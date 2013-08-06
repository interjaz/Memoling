using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SententceOptimizer
{
    public class Word
    {
        public string Language { get; private set; }
        public string Text { get; private set; }

        public Word(string language, string text)
        {
            Language = language;
            Text = text;
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
            {
                return false;
            }

            return obj.GetHashCode() == obj.GetHashCode();
        }

        private int? hashCode;

        public override int GetHashCode()
        {
            if (hashCode != null)
            {
                return hashCode.Value;
            }

            unchecked
            {
                int code = 31;

                if (Language != null)
                {
                    code *= Language.GetHashCode();
                }

                if (Text != null)
                {
                    code *= Text.GetHashCode();
                }

                hashCode = code;
                return code;
            }
        }
    }

    class Sentence
    {
        public int Id { get; set; }
        public Word[] Words { get; set; }
        public string Line { get; set; }
    }
}
