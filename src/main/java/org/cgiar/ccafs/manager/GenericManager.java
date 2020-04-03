/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning & 
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.manager;

import java.util.List;
import java.util.Optional;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public interface GenericManager<T, ID> {
  public List<T> findAll();
  public Optional<T> findById(ID id);
  public Long count();
  
  public T save (T entity) throws Exception;
  public T update (T entity) throws Exception;
  public void delete (T entity) throws Exception;
  public void deleteById (ID id) throws Exception;
  
  public void validate(T entity) throws Exception;
}
