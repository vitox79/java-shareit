package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.status.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);


    @Query("select b from Booking b " +
        "left join Item i on i.id = b.item " +
        "left join User u on i.owner = u.id " +
        "where u.id = ?1 and b.status in (?2) " +
        "order by b.start desc")
    List<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status);

    @Query("select b from Booking b " +
        "left join Item i on i.id = b.item " +
        "left join User u on i.owner = u.id " +
        "where u.id = ?1 " +
        "order by b.start desc")
    List<Booking> findAllByOwnerId(Long ownerId);


    @Query("select b from Booking b " +
        "left join Item i on i.id = b.item " +
        "left join User u on i.owner = u.id " +
        "where u.id = ?1 and b.status = ?2 " +
        "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatus(Long ownerId, Status status);

    List<Booking> findByBookerIdAndStatusIsOrderByStartDesc(long userId, Status waiting);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusInOrderByStartDesc(long userId, Set<Status> futureStatuses);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime now, LocalDateTime now1);

    Optional<Booking> findFirst1ByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime time);

    Optional<Booking> findFirst1ByItemIdAndStartAfterAndStatusNotLikeOrderByStartAsc(Long itemId, LocalDateTime time, Status status);

    Optional<List<Booking>> findByItemIdAndBookerIdAndEndBeforeAndStatusNotLike(Long itemId, Long bookerId, LocalDateTime time, Status status);

    @Query("select b from Booking b " +
        "left join Item i on i.id = b.item " +
        "left join User u on i.owner = u.id " +
        "where u.id = ?1 " +
        "order by b.start desc")
    List<Booking> findByOwnerId(long ownerId);

    List<Booking> findByItem_OwnerIdOrderByStartDesc(long ownerId);
    @Query("select b from Booking b " +
        "left join Item i on i.id = b.item " +
        "left join User u on i.owner = u.id " +
        "where u.id = ?1 and b.status in (?2) " +
        "order by b.start desc")
    List<Booking> findByOwnerIdAndStatusIn(long ownerId, Set<Status> waiting);

    @Query("select b from Booking b " +
        "left join Item i on i.id = b.item " +
        "left join User u on i.owner = u.id " +
        "where u.id = ?1 and b.status = ?2 " +
        "order by b.start desc")
    List<Booking> findByOwnerIdAndStatus(long ownerId, Status rejected);

    @Query("select b from Booking b " +
        "left join Item i on i.id = b.item " +
        "left join User u on i.owner = u.id " +
        "where u.id = ?1 and b.start < ?2 and b.end > ?2 " +
        "order by b.start desc")
    List<Booking> findByOwnerIdCurrent(long ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
        "left join Item i on i.id = b.item " +
        "left join User u on i.owner = u.id " +
        "where u.id = ?1 and b.end < ?2 " +
        "order by b.start desc")
    List<Booking> findByOwnerIdPast(long ownerId, LocalDateTime now);
}



