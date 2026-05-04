create view tournament_view AS 
SELECT 
   tm.id,
   tm.tournament_id,
   tm.modified AS "tournament_date",
   tm.player_person_id,
   CASE 
		WHEN tm.player_id = winner_id THEN
			tm.winner_victories
		ELSE tm.loser_victories
   END AS "player_wins",
   tm.opponent_person_id,
   CASE
		WHEN tm.opponent_id = winner_id THEN 
			tm.winner_victories
		ELSE tm.loser_victories
   END AS "opponent_wins",
   tm.draw,
   CASE
		WHEN tm.winner_id = tm.player_id THEN 
			player.id
		ELSE opponent.id
   END AS "winner_id",
   CASE
		WHEN tm.winner_id = tm.player_id THEN 
			opponent.id
		ELSE player.id
   END AS "loser_id",
   player.code AS "player_code",
   opponent.code AS "opponent_code",
   concat(tr.url_view, '/', tm.tournament_id) AS "url",
   tm.elo_calculated
FROM
    tournament_match tm
INNER JOIN person player ON tm.player_person_id = player.id
INNER JOIN person opponent ON tm.opponent_person_id = opponent.id
INNER JOIN tournament_round tr ON tr.id = tm.round_id
ORDER BY tm.tournament_id ASC, tm.id asc;