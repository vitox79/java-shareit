package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusIsOrderByStartDesc(long userId, Status waiting, Pageable pageable);

    Page<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusInOrderByStartDesc(long userId, Set<Status> futureStatuses, Pageable pageable);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime now,
                                                                          LocalDateTime now1, Pageable pageable);

    Optional<Booking> findFirst1ByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime time);

    Optional<Booking> findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(Long itemId, LocalDateTime time,
                                                                                     Status status);

    Optional<List<Booking>> findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(Long itemId, Long bookerId,
                                                                                LocalDateTime time, Status status);

    Page<Booking> findByItem_OwnerIdOrderByStartDesc(long ownerId, Pageable pageable);

    @Query("select b from Booking b " +
        "left join Item i on i.id = b.item " +
        "left join User u on i.owner = u.id " +
        "where u.id = ?1 and b.status in (?2) " +
        "order by b.start desc")
    Page<Booking> findByOwnerIdAndStatusIn(long ownerId, Set<Status> waiting, Pageable pageable);

    @Query("select b from Booking b " +
        "left join Item i on i.id = b.item " +
        "left join User u on i.owner = u.id " +
        "where u.id = ?1 and b.status = ?2 " +
        "order by b.start desc")
    Page<Booking> findByOwnerIdAndStatus(long ownerId, Status rejected, Pageable pageable);

    @Query("select b from Booking b " +
        "left join Item i on i.id = b.item " +
        "left join User u on i.owner = u.id " +
        "where u.id = ?1 and b.start < ?2 and b.end > ?2 " +
        "order by b.start desc")
    Page<Booking> findByOwnerIdCurrent(long ownerId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
        "left join Item i on i.id = b.item " +
        "left join User u on i.owner = u.id " +
        "where u.id = ?1 and b.end < ?2 " +
        "order by b.start desc")
    Page<Booking> findByOwnerIdPast(long ownerId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
        "JOIN FETCH b.item " +
        "WHERE b.status != 'REJECTED' " +
        "AND b.item IN :items " +
        "AND b.start > :now " +
        "ORDER BY b.start DESC, b.id DESC")
    List<Booking> findApprovedNextForItems(List<Item> items, Sort sort, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
        "JOIN FETCH b.item " +
        "WHERE b.status != 'REJECTED' " +
        "AND b.item IN :items " +
        "AND b.start < :now " +
        "ORDER BY b.start DESC, b.id DESC")
    List<Booking> findApprovedLastForItems(List<Item> items, Sort sort, LocalDateTime now);

    @Query("select b from Booking b " +
        "left join Item i on i.id = b.item " +
        "left join User u on i.owner = u.id " +
        "where u.id = ?1 ")
    Page<Booking> findByOwnerId(Long ownerId, Pageable pageable);
}




