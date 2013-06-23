using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace MemolingTools.VariableTranslator
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            textBox1.Text = Generate(textBox1.Text);
        }

        private string Generate(string str) {

            String str1 = "";
            String str2 = "";
            String str3 = "\tpublic String serialize() throws JSONException {" + System.Environment.NewLine + "\t\tJSONObject json = new JSONObject();" + System.Environment.NewLine+ System.Environment.NewLine;
            String str4 = "\tpublic XXX deserialize(JSONObject json) throws JSONException {" + System.Environment.NewLine + System.Environment.NewLine;


            foreach(string line in str.Split(new string[] { System.Environment.NewLine }, StringSplitOptions.None)) {

                str1 += line;
                str1 += System.Environment.NewLine;

                string[] words = line.Split(new string[] { " "} , StringSplitOptions.RemoveEmptyEntries);

                if (words.Length != 0)
                {
            
                    for (int i=0;i<words.Length;i++)
                    {
                        if (words[i].StartsWith("m_"))
                        {
                            string variable = words[i].Substring(2, words[i].Length - 3);
                            string word = words[i].Substring(0, words[i].Length - 1);
                            string varJType = "String";

                            if (words[i - 1] == "int")
                            {
                                varJType = "Int";
                            }
                            else if (words[i - 1] == "Date")
                            {
                                varJType = "Long";
                            }
                            else if (words[i - 1] == "Long")
                            {
                                varJType = "Long";
                            }
                            else if (words[i - 1] == "double" || words[i-1] == "float")
                            {
                                varJType = "Double";
                            }
                            else if (words[i - 1] == "boolean")
                            {
                                varJType = "Boolean";
                            }

                            // Getter 
                            str2 += "\tpublic " + words[i - 1] + " get" + UpFirst(variable) + "() { " +
                                " return " + words[i] + " }";

                            str2 += System.Environment.NewLine;

                            // Setter
                            str2 += "\tpublic void set" + UpFirst(variable) + "(" + words[i - 1] + " " + variable + ") { " +
                                 words[i].Substring(0, words[i].Length-1) + " = " + variable + "; }";

                            str2 += System.Environment.NewLine;

                            str3 += "\t\tjson.put(\"" + word + "\", " + word + ");" + System.Environment.NewLine;
                            str4 += "\t\t" + word + " = json.get" + varJType + "(\"" + word + "\");" + System.Environment.NewLine;

                            break;
                        }
                    }

                    str2 += System.Environment.NewLine; 
                }


            }
            str3 += System.Environment.NewLine + "\t\treturn json.toString();" + System.Environment.NewLine + "\t}" + System.Environment.NewLine;
            str4 += System.Environment.NewLine + "\t\treturn this;" + System.Environment.NewLine + "\t}" + System.Environment.NewLine;


            return str1 + str2 + str3 +  System.Environment.NewLine + str4;

        }

        private string UpFirst(string word)
        {
            return word[0].ToString().ToUpper() + word.Substring(1, word.Length-1);
        }
    }
}
