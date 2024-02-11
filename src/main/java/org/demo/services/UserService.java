package org.demo.services;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.demo.model.User;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class UserService {
  final private Logger logger = Logger.getLogger(UserService.class);
  final static private int DefaultSize = 10;
  final static private int DefaultPage = 0;

  public List<User> getAll(int page, int size) {
    PanacheQuery<PanacheEntityBase> users = User.findAll();
    return users.page(
        page <= 0 ? DefaultPage : page,
        size <= 0 ? DefaultSize : size)
        .list();
  }

  @Transactional
  public void create(User user) {
    if(user.id != null)
      throw new BadRequestException("id not allowed in body");
    user.persist();
  }

  @Transactional
  public void update(Long id, User user) {
    User nullable = User.findById(id);
    if(nullable == null) {
      throw new NotFoundException("user not found");
    }
    User.merge(nullable, user);
  }

  @Transactional
  public void delete(Long id) {
    if(!User.deleteById(id)) {
      throw new NotFoundException("user not found");
    }
  }
}
