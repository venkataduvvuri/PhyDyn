package phydyn.util;

/**
 * Basic Double Matrix class. Method names follow JBlas as much as possible.
 * The implementation allows for data buffers to be bigger than the matrix size.
 * A DMatrix can start from any index of the data buffer.
 * @author Igor Siveroni
 *
 */


public class DMatrix {
	public double[] data;
	public int rows,columns,length,start;

	public DMatrix(int inRows, int inColumns, double[] inData, int inStart) {
		if ((inRows<=0)||(inColumns<0))
			throw new IllegalArgumentException("DMatrix: negative rows/columns");
		length = inRows*inColumns;
		if ((inStart+length) > inData.length)
			throw new IllegalArgumentException("DMatrix: buffer too small");
		data = inData;
		rows=inRows;
		columns=inColumns;
		start=inStart;
	}
	
	public DMatrix(int inRows, int inColumns, double[] inData) {
		this(inRows,inColumns,inData,0);
	}
	
	public DMatrix(int inRows, int inColumns) {
		this(inRows,inColumns,new double[inRows*inColumns],0);
	}
	
	/* created for JBlas compatibility during code transition */
	public DMatrix() {
		this(1,1,new double[1],0);
	}
	
	// copy constructor
	public DMatrix(DMatrix M) {
		this(M.rows,M.columns);
		System.arraycopy(M.data, M.start, this.data, this.start, M.length);
	}
	
	public DMatrix transpose() {
		DMatrix R = new DMatrix(this.columns,this.rows);
		int idx, ridx;
		ridx = R.start; // 0
		for(int row=0; row<R.rows; row++) {
			idx = this.start+row;
			for(int col=0; col<R.columns; col++) {
				R.data[ridx] = this.data[idx];
				ridx++; idx += this.rows;
			}
		}
		return R;
	}

	public String toString() {
		String r = "[ ";
		for(int idx =start; idx < (start+length); idx++)
			r += data[idx]+" ";
		return r+"]";
	}
	
	public void put(int pos, double v) {
		if (pos > this.length)
			throw new IllegalArgumentException("Dmatrix: out if bounds indices");
		this.data[this.start+pos] = v;
	}
	
	public void put(int row, int col, double v) {
		if ((row>this.rows)||(col>this.columns))
			throw new IllegalArgumentException("Dmatrix: out if bounds indices");
		this.data[start+col*this.rows+row] = v;
	}
	
	public double get(int row, int col) {
		if ((row>this.rows)||(col>this.columns))
			throw new IllegalArgumentException("Dmatrix: out ff bounds indices");
		return this.data[start+col*this.rows+row];
	}
	
	public void mini(double v) {
		int idx = this.start;
		for(int i=0; i<rows*columns; i++) {
			if (this.data[idx] > v)
				this.data[idx] = v;
			idx++;
		}
	}
	
	public void maxi(double v) {
		int idx = this.start;
		for(int i=0; i<rows*columns; i++) {
			if (this.data[idx] < v)
				this.data[idx] = v;
			idx++;
		}
	}
	
	public void clampi(double vmin, double vmax) {
		int idx = this.start;
		for(int i=0; i<rows*columns; i++) {
			if (this.data[idx] > vmax)
				this.data[idx] = vmax;
			else if (this.data[idx] < vmin)
				this.data[idx] = vmin;
			idx++;
		}
	}
	
	
	public DVector columnSums() {
		DVector S = new DVector(this.columns);
		double sum;
		int idx=start;
		for(int col=0; col<this.columns; col++) {
			sum=0;
			for(int row=0; row<this.rows; row++) {
				sum += this.data[idx++];
			}
			S.data[col] = sum;
		}
		return S;
	}
	
	public DVector rowSums() {
		DVector S = new DVector(this.rows);
		int idx=start, sidx;
		for(int col=0; col<this.columns; col++) {	
			sidx = S.start;
			for(int row=0; row<this.rows; row++) {
				S.data[sidx] += this.data[idx];
				idx++; sidx++;
			}
		}
		return S;
	}
	
	public DMatrix divRowVector(DVector V) {
		if (V.length!=this.columns)
			throw new IllegalArgumentException("DMatrix diviRowVector: Incorrect vector length");
		DMatrix R = new DMatrix(this.rows,this.columns);
		int idx=this.start;
		int ridx = R.start;
		int vidx;
		double d;
		vidx = V.start;
		for(int col=0;col<this.columns;col++) {
			d = V.data[vidx];
			for(int row=0; row<this.rows; row++) {
				// this.data[idx] /= d;
				R.data[ridx] = this.data[idx] / d;
				idx++; ridx++;
			}
			vidx++;
		}
		return R;
	}
	
	public DMatrix diviRowVector(DVector V, DMatrix R) {
		if (V.length!=this.columns)
			throw new IllegalArgumentException("DMatrix diviRowVector: Incorrect vector length");
		
		int idx=this.start;
		int ridx = R.start;
		int vidx;
		double d;
		vidx = V.start;
		for(int col=0;col<this.columns;col++) {
			d = V.data[vidx];
			for(int row=0; row<this.rows; row++) {
				// this.data[idx] /= d;
				R.data[ridx] = this.data[idx] / d;
				idx++; ridx++;
			}
			vidx++;
		}
		return this;
	}
	
	public DMatrix divColumnVector(DVector V) {
		if (V.length != this.rows)
			throw new IllegalArgumentException("Wrong matrix size");
		DMatrix R = new DMatrix(this.rows, this.columns);
		int midx=this.start, vidx;
		for(int col=0; col < this.columns; col++) {
			vidx = V.start;
			for(int row=0; row < this.rows; row++) {
				R.data[midx] = this.data[midx] / V.data[vidx];
				midx++; vidx++;
			}
		}
		return R;
	}
	
	public DMatrix diviColumnVector(DVector V, DMatrix R) {
		if (V.length != this.rows)
			throw new IllegalArgumentException("Wrong matrix size");
		// check R dimensions?
		int midx=this.start, vidx, ridx=R.start;
		for(int col=0; col < this.columns; col++) {
			vidx = V.start;
			for(int row=0; row < this.rows; row++) {
				R.data[ridx] = this.data[midx] / V.data[vidx];
				midx++; vidx++; ridx++;
			}
		}
		return R;
	}
	
	public DMatrix diviRowVector(DVector V) {
		if (V.length!=this.columns)
			throw new IllegalArgumentException("DMatrix diviRowVector: Incorrect vector length");
		int idx=this.start;
		int vidx;
		double d;
		vidx = V.start;
		for(int col=0;col<this.columns;col++) {
			d = V.data[vidx];
			for(int row=0; row<this.rows; row++) {
				this.data[idx] /= d;
				idx++;
			}
			vidx++;
		}
		return this;
	}
	
	public DMatrix diviColumnVector(DVector V) {
		if (this.rows!=V.length)
			throw new IllegalArgumentException("DMatrix.diviColumnVector: incorrect vector length");
		int idx = this.start;
		int vidx;
		for(int col=0;col<this.columns;col++) {
			vidx = V.start;
			for(int row=0; row<this.rows; row++) {
				this.data[idx] /= V.data[vidx];
				idx++; vidx++;
			}
			
		}
		return this;
	}
	
	public DMatrix add(DMatrix M) {
		if ((this.columns!=M.columns)||(this.rows!=M.rows))
			throw new IllegalArgumentException("DMatrix.add: wrong dimensions");
		DMatrix R = new DMatrix(this.rows, this.columns);
		int ridx, idx, midx;
		ridx = R.start; idx = this.start; midx = M.start;
		for(int col=0; col < this.columns; col++) {  // could do rows*columns
			for(int row=0; row < this.rows; row++) {
				R.data[ridx] = this.data[idx] + M.data[midx];
				ridx++; idx++; midx++;
			}
		}
		return R;		
	}
	
	/* slow textbook version - start from here 
	 * then try matrix by vector version 
	 * and blocked version
	 */
	public DMatrix mmul(DMatrix other) {
		if (this.columns!=other.rows)
			throw new IllegalArgumentException("DMatrix.mmul: Wrong dimensions");
		DMatrix R = new DMatrix(this.rows,other.columns);
		int ridx = R.start;
		int idx, oidx;
		double sum;
		for(int col=0; col < R.columns; col++) {
			for(int row=0; row<R.rows; row++) {
				sum=0;
				idx = this.start+row;
				oidx = other.start+col*other.rows;
				// product
				for(int i=0; i<this.columns; i++) {
					sum += this.data[idx]*other.data[oidx];
					idx += this.rows;
					oidx++;
				}
				R.data[ridx] = sum;
				ridx++;
			}
		}
		return R;
	}
	
	public DMatrix mmuli(DMatrix other, DMatrix R) {
		if (this.columns!=other.rows)
			throw new IllegalArgumentException("DMatrix.mmul: Wrong dimensions");
		// DMatrix R = new DMatrix(this.rows,other.columns);
		int ridx = R.start;
		int idx, oidx;
		double sum;
		for(int col=0; col < R.columns; col++) {
			for(int row=0; row<R.rows; row++) {
				sum=0;
				idx = this.start+row;
				oidx = other.start+col*other.rows;
				// product
				for(int i=0; i<this.columns; i++) {
					sum += this.data[idx]*other.data[oidx];
					idx += this.rows;
					oidx++;
				}
				R.data[ridx] = sum;
				ridx++;
			}
		}
		return R;
	}
	
	/* Returns a subvector - test */
	/* add version that creates fresh vector or copies it to another vector*/
	public DVector getColumn(int col) {
		if (col>=this.columns)
			throw new IllegalArgumentException("DMatrix.getColumn: invalid col number");
		return new DVector(this.rows,this.data,this.start+this.rows*col);
	}
	
	
	public DVector getColumn(int col, boolean copy) {
		if (!copy)
			return this.getColumn(col);
		if (col>=this.columns)
			throw new IllegalArgumentException("DMatrix.getColumn: invalid col number");
		DVector R = new DVector(this.rows);
		System.arraycopy(this.data, this.start+this.rows*col, R.data, R.start, this.rows);
		return R;
	}
	
	
}