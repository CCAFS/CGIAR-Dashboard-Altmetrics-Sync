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

package org.cgiar.ccafs.manager.implementation;

import org.cgiar.ccafs.dao.DeliverableDAO;
import org.cgiar.ccafs.domain.marlo.Deliverable;
import org.cgiar.ccafs.manager.DeliverableManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class DeliverableManagerImpl implements DeliverableManager {

  @Autowired
  DeliverableDAO deliverableDAO;

  @Autowired
  Validator validator;

  @Override
  @Transactional(readOnly = true)
  public Long count() {
    return deliverableDAO.count();
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void delete(Deliverable entity) throws Exception {
    String id = entity.getId();
    if (StringUtils.isBlank(id)) {
      throw new Exception("Detached entity");
    }

    if (deliverableDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    entity = deliverableDAO.findById(id).get();

    // deliverableDAO.delete(entity);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void deleteById(String id) throws Exception {
    if (StringUtils.isBlank(id)) {
      throw new Exception("ID can not be null");
    }

    if (deliverableDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    // delete(findById(id).get());
  }

  @Override
  @Transactional(readOnly = true)
  public List<Deliverable> findAll() {
    return deliverableDAO.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Deliverable> findById(String id) {
    id = StringUtils.stripToEmpty(id);
    return deliverableDAO.findById(id);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Deliverable save(Deliverable entity) throws Exception {
    this.validate(entity);
    deliverableDAO.save(entity);
    return entity;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Deliverable update(Deliverable entity) throws Exception {
    this.validate(entity);

    String id = entity.getId();
    if (StringUtils.isBlank(id)) {
      throw new Exception("Detached entity");
    }

    if (deliverableDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    deliverableDAO.save(entity);
    return entity;
  }

  @Override
  public void validate(Deliverable entity) throws Exception {
    Objects.requireNonNull(entity);

    Set<ConstraintViolation<Deliverable>> constraintViolations = validator.validate(entity);

    if (!constraintViolations.isEmpty()) {
      StringBuilder strMessage = new StringBuilder();

      for (ConstraintViolation<Deliverable> constraintViolation : constraintViolations) {
        strMessage.append(constraintViolation.getPropertyPath().toString());
        strMessage.append(" - ");
        strMessage.append(constraintViolation.getMessage());
        strMessage.append(". \n");
      }

      throw new Exception(strMessage.toString());
    }
  }

}
