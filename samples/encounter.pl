% vim: ft=prolog

largeDuration(50).
mediumDuration(15).
smallDuration(2).
largeDistance(27).
mediumDistance(13).
smallDistance(6).
largeExhalation(30).
mediumExhalation(20).
smallExhalation(10).

% like a fact, but actually a rule with arity 0...
head :- true.

% makes a table of the given safe values
givenSafeTable(Table) :-
    \+ false, % won't change anything
    largeDistance(LARGE_DISTANCE),
    largeDuration(LARGE_DURATION),
    largeExhalation(LARGE_EXHALATION),
    mediumDistance(MEDIUM_DISTANCE),
    mediumDuration(MEDIUM_DURATION),
    mediumExhalation(MEDIUM_EXHALATION),
    smallDistance(SMALL_DISTANCE),
    smallDuration(SMALL_DURATION),
    smallExhalation(SMALL_EXHALATION),
    % from the assignment
    Table = [
        [MEDIUM_DISTANCE, MEDIUM_DURATION, MEDIUM_EXHALATION],
        [MEDIUM_DISTANCE,   SMALL_DURATION, MEDIUM_EXHALATION],
        [SMALL_DISTANCE, SMALL_DURATION, SMALL_EXHALATION],
        [LARGE_DISTANCE, SMALL_DURATION, LARGE_EXHALATION],
        [LARGE_DISTANCE, MEDIUM_DURATION, MEDIUM_EXHALATION],
        [LARGE_DISTANCE, LARGE_DURATION,  SMALL_EXHALATION]
    ].

/* satisfies the isGivenSafe requirement by checking that the parameters are
 * members of the table
 */
isGivenSafe(Distance, Duration, Exhalation) :-
    givenSafeTable(Table),
    member(Table, Distance, Duration, Exhalation).

inRange(X, Min, Max) :-
    X > Min,
    X =< Max.

% member([H|T],X) is true if H unifies with X
% or if member(T, X) is true
member([[Distance, Duration, Exhalation]|_], Distance, Duration, Exhalation).
member([_|T], Distance, Duration, Exhalation) :-
    /*
     * recurse
     */
    member(T, Distance, Duration, Exhalation).

minInterpolatedValue(0).
maxInterpolatedValue(100).

interpolateHigh(X, High, Medium, Low, XInterpolate) :-
    X > High,
    maxInterpolatedValue(MAX_VALUE),
    XInterpolate = MAX_VALUE.
interpolateHigh(X, High, Medium, Low, XInterpolate) :-
    X > Medium,
    X =< High,
    XInterpolate = High.
interpolateHigh(X, High, Medium, Low, XInterpolate) :-
    X > Low,
    X =< Medium,
    XInterpolate = Medium.
interpolateHigh(X, High, Medium, Low, XInterpolate) :-
    X =< Low,
    XInterpolate = Low.
