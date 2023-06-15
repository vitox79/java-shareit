package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.status.Status;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId);


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

}




