/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.os.search.android;

import android.database.Cursor;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Emits a {@link android.database.Cursor} for every available position.
 */
final class OnSubscribeCursor implements ObservableOnSubscribe<Cursor> {

  private final Cursor cursor;

  OnSubscribeCursor(final Cursor cursor) {
    this.cursor = cursor;
  }

  @Override
  public void subscribe(ObservableEmitter<Cursor> emitter) throws Exception {
    try {
      while (!emitter.isDisposed() && cursor.moveToNext()) {
        emitter.onNext(cursor);
      }
      if (!emitter.isDisposed()) {
        emitter.onComplete();
      }
    } catch (Throwable e) {
      if (!emitter.isDisposed()) {
        emitter.onError(e);
      }
    } finally {
      if (!cursor.isClosed()) {
        cursor.close();
      }
    }
  }
}
