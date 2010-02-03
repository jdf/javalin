/*
   Copyright 2008 Jonathan Feinberg

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package javalin.draw;

/**
 * NOT thread-safe
 * @author jdf
 *
 */
class FloatList
{
	private float[] list;
	private int size = 0;

	public FloatList(final int initialCapacity)
	{
		list = new float[initialCapacity];
	}

	public FloatList()
	{
		this(16000);
	}

	public float[] toArray()
	{
		final float[] result = new float[size()];
		System.arraycopy(list, 0, result, 0, size());
		return result;
	}

	public float get(final int index)
	{
		if (index >= size)
			throw new ArrayIndexOutOfBoundsException("Attempt to fetch item " + index
					+ " in array of size " + size);
		return list[index];
	}

	public void add(final float f)
	{
		ensureCapacity(size + 1);
		list[size++] = f;
	}

	public void add(final float f, final float g, final float h)
	{
		ensureCapacity(size + 3);
		list[size++] = f;
		list[size++] = g;
		list[size++] = h;
	}

	public int size()
	{
		return size;
	}

	private void ensureCapacity(final int minCapacity)
	{
		final int oldCapacity = list.length;
		if (minCapacity > oldCapacity)
		{
			final float oldData[] = list;
			int newCapacity = oldCapacity * 3 / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			list = new float[newCapacity];
			System.arraycopy(oldData, 0, list, 0, size);
		}
	}
}
