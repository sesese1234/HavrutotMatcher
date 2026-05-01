Add-Type -AssemblyName System.Drawing
$img = [System.Drawing.Image]::FromFile("C:\Users\Yossi\.gemini\antigravity\brain\1a21a539-66a3-4450-b47f-bf5ccebb7b09\havrutot_icon_1777624931281.png")
$bmp = New-Object System.Drawing.Bitmap($img, 256, 256)
$icoFile = "app\icon.ico"
$stream = [System.IO.File]::OpenWrite($icoFile)
[System.Drawing.Icon]::FromHandle($bmp.GetHicon()).Save($stream)
$stream.Close()
$bmp.Dispose()
$img.Dispose()
