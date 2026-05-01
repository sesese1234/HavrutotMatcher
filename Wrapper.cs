using System;
using System.IO;
using System.IO.Compression;
using System.Diagnostics;
using System.Reflection;

class Program
{
    static void Main()
    {
        string extractPath = Path.Combine(Path.GetTempPath(), "HavrutotMatcherApp_" + Guid.NewGuid().ToString().Substring(0,8));
        string exePath = Path.Combine(extractPath, "HavrutotMatcher.exe");

        Directory.CreateDirectory(extractPath);

        try 
        {
            using (Stream stream = Assembly.GetExecutingAssembly().GetManifestResourceStream("payload.zip"))
            using (FileStream fileStream = new FileStream(Path.Combine(extractPath, "payload.zip"), FileMode.Create))
            {
                stream.CopyTo(fileStream);
            }

            ZipFile.ExtractToDirectory(Path.Combine(extractPath, "payload.zip"), extractPath);
            File.Delete(Path.Combine(extractPath, "payload.zip"));

            string wrapperDir = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);

            ProcessStartInfo psi = new ProcessStartInfo();
            psi.FileName = exePath;
            psi.WorkingDirectory = extractPath;
            psi.EnvironmentVariables["WRAPPER_DIR"] = wrapperDir;
            psi.UseShellExecute = false;

            Process process = Process.Start(psi);
            process.WaitForExit();
        }
        catch (Exception ex) 
        {
            File.WriteAllText(Path.Combine(Path.GetTempPath(), "havrutot_error.txt"), ex.ToString());
        }
        finally 
        {
            try { Directory.Delete(extractPath, true); } catch {}
        }
    }
}
