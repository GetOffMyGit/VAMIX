
public class TextInfo {
	// font
	// what text
	// size etc
	private int _color;
	private int _size;
	private int _font;
	private String _text;
	private Integer _integerDuration;
	
	public TextInfo() {
		_font = 0;
		_color = 0;
		_size = 0;
		_integerDuration = 10;
		_text = "";
	}
	
	public TextInfo(int color, int size, int font, String text, Integer duration) {
		this._color = color;
		this._font = font;
		this._size = size;
		this._text = text;
		this._integerDuration = duration;
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
	
	
	
}
