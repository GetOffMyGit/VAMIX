
public class TextInfo{
	// font
	// what text
	// size etc
	private int _color;
	private int _size;
	private int _font;
	private String _text;
	private Integer _integerDuration;
	private String _colort;
	private String _sizet;
	private String _fontt;

	public TextInfo(int color, int size, int font, String text, Integer duration) {
		this._color = color;
		this._font = font;
		this._size = size;
		this._text = text;
		this._integerDuration = duration;
	}

	public TextInfo() {
		_font = 0;
		_color = 0;
		_size = 0;
		_integerDuration = 10;
		_text = "";
		_colort = "Red";
		_sizet = "14";
		_fontt = "FreeMono";
	}
	
	public TextInfo(int color, int size, int font, String text, Integer integerDuration, String colort, String sizet, String fontt) {
		this._color = color;
		this._font = font;
		this._size = size;
		this._text = text;
		this._integerDuration = integerDuration;
		_colort = colort;
		_sizet = sizet;
		_fontt = fontt;
	}

	public int get_color() {
		return _color;
	}

	public int get_size() {
		return _size;
	}

	public int get_font() {
		return _font;
	}

	public String get_text() {
		return _text;
	}
	
	public Integer get_integerDuration() {
		return _integerDuration;
	}
	
	public String toCommandIntro() {
		
		
		String s = "-vf \"drawtext=fontfile=/usr/share/fonts/truetype/freefont/" + _fontt + ".ttf:text='" + _text + "':fontsize=" + _sizet + ":fontcolor=" + _colort + ":draw='lt(t," + _integerDuration + ")' , ";
		return s;		
		//+ ":fontsize=" + _fontt + ":fontcolor=" + _colort + "\" ";
		
	}
	
	public String toCommandOutro() {
		int s = CurrentFile.getInstance().getDurationSeconds() - (int)_integerDuration;
		return "drawtext=fontfile=/usr/share/fonts/truetype/freefont/" + _fontt + ".ttf:text='" + _text + "':fontsize=" + _sizet + ":fontcolor=" + _colort + ":draw='lt(" + s + ",t)'\" ";
				//+ ":fontsize=" + _fontt + ":fontcolor=" + _colort + "\" ";
		
	}
	
	public String forFile() {
		return _color + "#" + _size + "#" + _font + "#" + _text + "#" + _integerDuration + "#" + _colort + "#" + _sizet + "#" + _fontt;
	}

	
	//avconv -i a.mp4 -strict experimental -i b.mp3 -strict experimental -vf drawtext="fontfile=/usr/share/fonts/truetype/freefont/FreeSerif.ttf:text='hello there':draw='lt(t,10)'" -filter_complex amix=inputs=2 lalal.mp4
	//avconv -i a.mp4 -strict experimental -i b.mp3 -strict experimental -vf drawtext="fontfile=/usr/share/fonts/truetype/freefont/FreeMono.tff:text='ohmy':fontsize=14:fontcolor=Red:draw='lt(t,10)'" 
	//avconv -i /afs/ec.auckland.ac.nz/users/c/e/ceir349/unixhome/a.mp4 -strict experimental -vf "drawtext=fontfile=/usr/share/fonts/truetype/freefont/FreeMono.ttf:text='love you':fontsize=28:fontcolor=Red:draw='lt(t,10)' , drawtext=fontfile=/usr/share/fonts/truetype/freefont/FreeMono.ttf:text='xxxxxaaaaaa':fontsize=28:fontcolor=Red:draw='lt(586,t)'" -filter_complex amix=inputs=1:duration=longest /afs/ec.auckland.ac.nz/users/c/e/ceir349/unixhome/work2.mp4

}
